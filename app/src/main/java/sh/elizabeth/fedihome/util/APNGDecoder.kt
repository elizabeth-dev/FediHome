package sh.elizabeth.fedihome.util

import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.github.penfeizhou.animation.apng.APNGDrawable
import com.github.penfeizhou.animation.apng.decode.APNGParser
import com.github.penfeizhou.animation.io.ByteBufferReader
import com.github.penfeizhou.animation.io.StreamReader
import com.github.penfeizhou.animation.loader.Loader
import okio.BufferedSource
import java.nio.ByteBuffer

// https://github.com/coil-kt/coil/issues/506#issuecomment-952526682
class AnimatedPngDecoder(private val source: ImageSource) : Decoder {

	override suspend fun decode(): DecodeResult {
		// We must buffer the source into memory as APNGDrawable decodes
		// the image lazily at draw time which is prohibited by Coil.
		val buffer = source.source().squashToDirectByteBuffer()
		return DecodeResult(
			image = APNGDrawable(Loader { ByteBufferReader(buffer) }).asImage(),
			isSampled = false,
		)
	}

	private fun BufferedSource.squashToDirectByteBuffer(): ByteBuffer {
		// Squash bytes to BufferedSource inner buffer then we know total byteCount.
		request(Long.MAX_VALUE)

		val byteBuffer = ByteBuffer.allocateDirect(buffer.size.toInt())
		while (!buffer.exhausted()) buffer.read(byteBuffer)
		byteBuffer.flip()
		return byteBuffer
	}

	class Factory : Decoder.Factory {

		override fun create(
			result: SourceFetchResult,
			options: Options,
			imageLoader: ImageLoader,
		): Decoder? {
			val stream = result.source.source().peek().inputStream()
			if (APNGParser.isAPNG(StreamReader(stream))) {
				return AnimatedPngDecoder(result.source)
			} else {
				return null
			}
		}
	}
}