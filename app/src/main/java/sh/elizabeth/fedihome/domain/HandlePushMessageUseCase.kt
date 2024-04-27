package sh.elizabeth.fedihome.domain

import android.security.keystore.KeyProperties
import sh.elizabeth.fedihome.data.repository.InternalDataRepository
import sh.elizabeth.fedihome.data.repository.PushNotificationRepository
import sh.elizabeth.fedihome.util.serializePublicKey
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

val P256_HEAD: ByteArray = byteArrayOf(
	0x30.toByte(),
	0x59.toByte(),
	0x30.toByte(),
	0x13.toByte(),
	0x06.toByte(),
	0x07.toByte(),
	0x2a.toByte(),
	0x86.toByte(),
	0x48.toByte(),
	0xce.toByte(),
	0x3d.toByte(),
	0x02.toByte(),
	0x01.toByte(),
	0x06.toByte(),
	0x08.toByte(),
	0x2a.toByte(),
	0x86.toByte(),
	0x48.toByte(),
	0xce.toByte(),
	0x3d.toByte(),
	0x03.toByte(),
	0x01.toByte(),
	0x07.toByte(),
	0x03.toByte(),
	0x42.toByte(),
	0x00.toByte()
)

val kf = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC)
val ecdh = KeyAgreement.getInstance("ECDH")
val hmacContext = Mac.getInstance("HmacSHA256")
val cipher = Cipher.getInstance("AES/GCM/NoPadding")

class HandlePushMessageUseCase @Inject constructor(
	private val pushNotificationRepository: PushNotificationRepository,
	private val internalDataRepository: InternalDataRepository,
) {
	@OptIn(ExperimentalEncodingApi::class) suspend operator fun invoke(
		pushAccountId: String,
		k: String,
		p: String,
		s: String,
	) {
		val (_, account) =
			internalDataRepository.getAccountByPushAccountId(pushAccountId)
				.takeIf { it?.value?.pushData?.pushPrivateKey != null && it.value.pushData.pushAuthKey != null && it.value.pushData.pushPublicKey != null }
				?: return

		val serverKey = getServerKey(k)
		val privKey = getPrivateKey(account.pushData.pushPrivateKey!!)
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

		println(decryptedStr)
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
