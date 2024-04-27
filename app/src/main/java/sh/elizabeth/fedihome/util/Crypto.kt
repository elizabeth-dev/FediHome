package sh.elizabeth.fedihome.util

import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.util.Arrays

object CryptoConstants {
	const val KEY_SIZE = 32
	const val UNCOMPRESSED_INDICATOR: Byte = 4
}

fun serializePublicKey(key: PublicKey): ByteArray {
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