package sh.elizabeth.fedihome.util

import android.security.keystore.KeyProperties
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.Mac

object CryptoConstants {
	const val KEY_SIZE = 32
	const val UNCOMPRESSED_INDICATOR: Byte = 4
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
}

fun publicKeyToEncodedECPoint(key: PublicKey): ByteArray {
	val point = (key as ECPublicKey).w
	var x = point.affineX.toByteArray()
	var y = point.affineY.toByteArray()
	if (x.size > CryptoConstants.KEY_SIZE) {
		x = Arrays.copyOfRange(x, x.size - CryptoConstants.KEY_SIZE, x.size)
	}
	if (y.size > CryptoConstants.KEY_SIZE) {
		y = Arrays.copyOfRange(y, y.size - CryptoConstants.KEY_SIZE, y.size)
	}
	val result = ByteArray(65)
	result[0] = CryptoConstants.UNCOMPRESSED_INDICATOR
	System.arraycopy(
		x, 0, result, 1 + (CryptoConstants.KEY_SIZE - x.size), x.size
	)
	System.arraycopy(y, 0, result, result.size - y.size, y.size)

	return result
}