package sh.elizabeth.fedihome.data.datasource

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Data
import kotlinx.serialization.json.Json
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.data.datasource.model.InternalDataAccount
import sh.elizabeth.fedihome.domain.P256_HEAD
import sh.elizabeth.fedihome.domain.cipher
import sh.elizabeth.fedihome.domain.ecdh
import sh.elizabeth.fedihome.domain.hmacContext
import sh.elizabeth.fedihome.domain.kf
import sh.elizabeth.fedihome.model.PushNotification
import sh.elizabeth.fedihome.model.notify
import sh.elizabeth.fedihome.util.SupportedInstances
import sh.elizabeth.fedihome.util.serializePublicKey
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

			else -> TODO()
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

	@OptIn(ExperimentalEncodingApi::class) fun handleMastodonNotification(
		accountId: String,
		instance: String,
		account: InternalDataAccount,
		messageData: Data,
	): PushNotification {
		// FIXME: handle missing fields
		val p = messageData.getString("p") ?: return TODO()
		val s = messageData.getString("s") ?: return TODO()
		val k = messageData.getString("k") ?: return TODO()

		val serverKey = getServerKey(k)
		val privKey =
			getPrivateKey(account.pushData.pushPrivateKey!!) // FIXME: handle missing fields
		val pubKey = getPublicKey(account.pushData.pushPublicKey!!)
		val authKey = getAuthKey(account.pushData.pushAuthKey!!)
		val salt = getSalt(s)

		val sharedSecret = getSharedSecret(privKey, serverKey)
		val secondSalt = getSecondSalt(authKey, sharedSecret)
		val key = deriveKey(
			salt, secondSalt, getInfo("aesgcm", pubKey, serverKey), 16
		)
		val nonce =
			deriveKey(salt, secondSalt, getInfo("nonce", pubKey, serverKey), 12)

		val aesKey = SecretKeySpec(key, "AES")
		val iv = GCMParameterSpec(128, nonce)

		cipher.init(Cipher.DECRYPT_MODE, aesKey, iv)
		val decrypted = cipher.doFinal(Base64.decode(p))
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

	@OptIn(ExperimentalEncodingApi::class) private fun getPrivateKey(
		encodedPrivKey: String,
	): PrivateKey = kf.generatePrivate(
		PKCS8EncodedKeySpec(
			Base64.UrlSafe.decode(encodedPrivKey)
		)
	)

	@OptIn(ExperimentalEncodingApi::class) private fun getPublicKey(
		encodedPubKey: String,
	): PublicKey = kf.generatePublic(
		X509EncodedKeySpec(
			Base64.UrlSafe.decode(encodedPubKey)
		)
	)

	@OptIn(ExperimentalEncodingApi::class)
	private fun getAuthKey(encodedAuthKey: String): ByteArray =
		Base64.UrlSafe.decode(encodedAuthKey)

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

	private fun getSecondSalt(
		authKey: ByteArray,
		sharedKey: ByteArray,
	): ByteArray = deriveKey(
		authKey,
		sharedKey,
		"Content-Encoding: auth${0.toChar()}".toByteArray(),
		32
	)

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

	private fun getInfo(
		type: String,
		clientPublicKey: PublicKey,
		serverPublicKey: PublicKey,
	): ByteArray {
		val info = ByteArrayOutputStream()
		try {
			info.write("Content-Encoding: ".toByteArray(StandardCharsets.UTF_8))
			info.write(type.toByteArray(StandardCharsets.UTF_8))
			info.write(0)
			info.write("P-256".toByteArray(StandardCharsets.UTF_8))
			info.write(0)
			info.write(0)
			info.write(65)
			info.write(serializePublicKey(clientPublicKey))
			info.write(0)
			info.write(65)
			info.write(serializePublicKey(serverPublicKey))
		} catch (ignore: IOException) {
		}
		return info.toByteArray()
	}
}