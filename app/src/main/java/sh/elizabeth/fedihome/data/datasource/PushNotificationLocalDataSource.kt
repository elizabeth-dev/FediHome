package sh.elizabeth.fedihome.data.datasource

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Data
import kotlinx.serialization.json.Json
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.data.datasource.model.InternalDataAccount
import sh.elizabeth.fedihome.model.PushNotification
import sh.elizabeth.fedihome.model.notify
import sh.elizabeth.fedihome.util.CryptoConstants.P256_HEAD
import sh.elizabeth.fedihome.util.CryptoConstants.cipher
import sh.elizabeth.fedihome.util.CryptoConstants.ecdh
import sh.elizabeth.fedihome.util.CryptoConstants.hmacContext
import sh.elizabeth.fedihome.util.CryptoConstants.kf
import sh.elizabeth.fedihome.util.SupportedInstances
import sh.elizabeth.fedihome.util.publicKeyToEncodedECPoint
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import sh.elizabeth.fedihome.api.mastodon.model.PushNotification_Message as MastodonPushNotification_Message

enum class EncryptionVersion {
	FINAL, DRAFT_04
}

/*
* Useful docs for Webpush encryption
*
* Mastodon's implementation (RFC8291-draft-04): https://datatracker.ietf.org/doc/html/draft-ietf-webpush-encryption-04
* Misskey's and co. implementation (RFC8291): https://datatracker.ietf.org/doc/html/rfc8291
* Embedded parameters in the payload as per final RFC8291: https://datatracker.ietf.org/doc/html/rfc8188#section-2.1
* ECE example https://webpush-wg.github.io/webpush-encryption/ben-comment/draft-ietf-webpush-encryption.html#rfc.section.5
*/
class PushNotificationLocalDataSource @Inject constructor() {
	fun handleNotification(
		accountId: String,
		account: InternalDataAccount,
		instance: String,
		instanceType: SupportedInstances,
		messageData: Data,
		context: Context,
	) {
		val pushNotification: PushNotification = when (instanceType) {
			SupportedInstances.MASTODON, SupportedInstances.GLITCH -> handleMastodonNotification(
				accountId, instance, account, messageData
			)

			SupportedInstances.SHARKEY, SupportedInstances.ICESHRIMP -> handleKeyNotification(
				accountId, instance, account, messageData
			)
		}

		createNotificationChannel(
			context, pushNotification.accountIdentifier
		) // FIXME: move to account creation

		pushNotification.notify(context)
	}

	fun createNotificationChannel(context: Context, channelId: String) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name =
				"Notification Channel name" // FIXME: set proper channel names, descriptions, and importances
			val descriptionText = "Notification Channel description"
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val channel =
				NotificationChannel(channelId, name, importance).apply {
					description = descriptionText
				}
			// Register the channel with the system.
			val notificationManager: NotificationManager =
				context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(channel)
		}
	}

	@OptIn(ExperimentalEncodingApi::class)
	fun handleKeyNotification(
		accountId: String,
		instance: String,
		account: InternalDataAccount,
		messageData: Data,
	): PushNotification {
		// FIXME: handle missing fields
		val rawPayload = messageData.getString("p") ?: return TODO()
		val rawServerKey = account.pushData.pushServerKey ?: return TODO()

		val payloadBytes =
			Base64.decode(rawPayload) // TODO: maybe use UrlSafe always
		val salt = getSaltFromPayload(payloadBytes)
		val rawContent = getContentFromPayload(payloadBytes)

		return handleWebpushNotification(
			accountId = accountId,
			instance = instance,
			account = account,
			payload = rawContent,
			salt = salt,
			rawServerKey = rawServerKey,
			encryptionVersion = EncryptionVersion.FINAL
		)
	}

	@OptIn(ExperimentalEncodingApi::class)
	fun handleMastodonNotification(
		accountId: String,
		instance: String,
		account: InternalDataAccount,
		messageData: Data,
	): PushNotification {
		// FIXME: handle missing fields
		val rawPayload = messageData.getString("p") ?: return TODO()
		val rawSalt = messageData.getString("s") ?: return TODO()
		val rawServerKey = messageData.getString("k") ?: return TODO()

		return handleWebpushNotification(
			accountId = accountId,
			instance = instance,
			account = account,
			payload = Base64.decode(rawPayload),
			salt = getSalt(rawSalt),
			rawServerKey = rawServerKey,
			encryptionVersion = EncryptionVersion.DRAFT_04
		)
	}

	fun handleWebpushNotification(
		accountId: String,
		instance: String,
		account: InternalDataAccount,
		payload: ByteArray,
		salt: ByteArray,
		rawServerKey: String,
		encryptionVersion: EncryptionVersion,
	): PushNotification {
		val serverKey = getServerKey(rawServerKey)
		val privateKey =
			getPrivateKey(account.pushData.pushPrivateKey!!) // FIXME: handle missing fields
		val publicKey = getPublicKey(account.pushData.pushPublicKey!!)
		val authSecret = getAuthSecret(account.pushData.pushAuthSecret!!)

		val sharedSecret = getSharedSecret(privateKey, serverKey)
		val inputKeyringMaterial = getIKM(
			authKey = authSecret,
			sharedKey = sharedSecret,
			clientPublicKey = publicKey,
			serverPublicKey = serverKey,
			encryptionVersion = encryptionVersion
		)
		val contentEncryptionKey = deriveKey(
			firstSalt = salt,
			secondSalt = inputKeyringMaterial,
			info = getContentEncodingInfo(
				type = if (encryptionVersion == EncryptionVersion.DRAFT_04) "aesgcm" else "aes128gcm",
				clientPublicKey = publicKey,
				serverPublicKey = serverKey,
				appendContext = encryptionVersion == EncryptionVersion.DRAFT_04
			),
			length = 16
		)
		val nonce = deriveKey(
			firstSalt = salt,
			secondSalt = inputKeyringMaterial,
			info = getContentEncodingInfo(
				type = "nonce",
				clientPublicKey = publicKey,
				serverPublicKey = serverKey,
				appendContext = encryptionVersion == EncryptionVersion.DRAFT_04
			),
			length = 12
		)

		val aesKey = SecretKeySpec(contentEncryptionKey, "AES")
		val iv = GCMParameterSpec(128, nonce)

		cipher.init(Cipher.DECRYPT_MODE, aesKey, iv)
		val decrypted = cipher.doFinal(payload)
		val decryptedStr =
			String(decrypted, 2, decrypted.size - 2, StandardCharsets.UTF_8)

		return Json.decodeFromString<MastodonPushNotification_Message>(
			decryptedStr
		).toDomain(accountId, instance)
	}

	@OptIn(ExperimentalEncodingApi::class)
	private fun getServerKey(encodedKey: String): PublicKey =
		Base64.UrlSafe.decode(encodedKey).let {
			val output = ByteArrayOutputStream()
			output.write(P256_HEAD)

			if (it.size == 64) output.write(4)

			output.write(it)

			kf.generatePublic(X509EncodedKeySpec(output.toByteArray()))
		}

	@OptIn(ExperimentalEncodingApi::class)
	private fun getPrivateKey(
		encodedPrivKey: String,
	): PrivateKey = kf.generatePrivate(
		PKCS8EncodedKeySpec(
			Base64.UrlSafe.decode(encodedPrivKey)
		)
	)

	@OptIn(ExperimentalEncodingApi::class)
	private fun getPublicKey(
		encodedPubKey: String,
	): PublicKey = kf.generatePublic(
		X509EncodedKeySpec(
			Base64.UrlSafe.decode(encodedPubKey)
		)
	)

	@OptIn(ExperimentalEncodingApi::class)
	private fun getAuthSecret(
		encodedAuthSecret: String,
	): ByteArray = Base64.UrlSafe.decode(encodedAuthSecret)

	private fun getSharedSecret(
		privKey: PrivateKey,
		serverKey: PublicKey,
	): ByteArray {
		ecdh.init(privKey)
		ecdh.doPhase(serverKey, true)
		return ecdh.generateSecret()
	}

	@OptIn(ExperimentalEncodingApi::class)
	private fun getSalt(encodedSalt: String) =
		Base64.UrlSafe.decode(encodedSalt)

	private fun getIKM(
		authKey: ByteArray,
		sharedKey: ByteArray,
		clientPublicKey: PublicKey,
		serverPublicKey: PublicKey,
		encryptionVersion: EncryptionVersion,
	): ByteArray = deriveKey(
		authKey,
		sharedKey,
		if (encryptionVersion == EncryptionVersion.DRAFT_04) getContentEncodingInfo(
			type = "auth",
			clientPublicKey = clientPublicKey,
			serverPublicKey = serverPublicKey,
			appendContext = false
		) else getWebpushInfo(
			clientPublicKey = clientPublicKey, serverPublicKey = serverPublicKey
		),
		32
	)

	private fun getWebpushInfo(
		clientPublicKey: PublicKey,
		serverPublicKey: PublicKey,
	): ByteArray {
		val info = ByteArrayOutputStream()
		try {
			info.write("WebPush: info".toByteArray(StandardCharsets.UTF_8))
			info.write(0)
			info.write(publicKeyToEncodedECPoint(clientPublicKey))
			info.write(publicKeyToEncodedECPoint(serverPublicKey))
		} catch (ignore: IOException) {
		}
		return info.toByteArray()
	}

	private fun deriveKey(
		firstSalt: ByteArray,
		secondSalt: ByteArray,
		info: ByteArray,
		length: Int,
	): ByteArray {
		hmacContext.init(SecretKeySpec(firstSalt, "HmacSHA256"))
		val hmac = hmacContext.doFinal(secondSalt)

		hmacContext.init(SecretKeySpec(hmac, "HmacSHA256"))
		hmacContext.update(info)
		val result = hmacContext.doFinal(byteArrayOf(1))

		if (result.size <= length) return result
		return Arrays.copyOfRange(
			result, 0, length
		)
	}

	private fun getContentEncodingInfo(
		type: String,
		clientPublicKey: PublicKey,
		serverPublicKey: PublicKey,
		appendContext: Boolean = false,
	): ByteArray {
		val info = ByteArrayOutputStream()
		try {
			info.write("Content-Encoding: ".toByteArray(StandardCharsets.UTF_8))
			info.write(type.toByteArray(StandardCharsets.UTF_8))
			info.write(0)
			if (appendContext) {
				info.write("P-256".toByteArray(StandardCharsets.UTF_8))
				info.write(0)
				info.write(0)
				info.write(65)
				info.write(publicKeyToEncodedECPoint(clientPublicKey))
				info.write(0)
				info.write(65)
				info.write(publicKeyToEncodedECPoint(serverPublicKey))
			}
		} catch (ignore: IOException) {
		}
		return info.toByteArray()
	}

	private fun getSaltFromPayload(payload: ByteArray): ByteArray =
		Arrays.copyOfRange(payload, 0, 16)

	private fun getContentFromPayload(payload: ByteArray): ByteArray {
		val idLen = payload[20].toUInt()
		return Arrays.copyOfRange(
			payload, idLen.plus(21u).toInt(), payload.size
		)
	}
}
