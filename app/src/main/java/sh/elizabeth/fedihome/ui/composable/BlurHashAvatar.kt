package sh.elizabeth.fedihome.ui.composable

import android.graphics.Bitmap
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.vanniktech.blurhash.BlurHash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BlurHashAvatar(modifier: Modifier = Modifier, imageUrl: String?, imageBlur: String?, imageSize: Dp = 48.dp, roundingRadius: Int = 16) {
	val resources = LocalContext.current.resources
	val imageSizeInPx =
		resources.displayMetrics.densityDpi.div(160f).times(imageSize.value).roundToInt()
	var imageBlurHash by remember { mutableStateOf<Bitmap?>(null) }

	LaunchedEffect(imageBlur) {
		CoroutineScope(Dispatchers.IO).launch {
			imageBlurHash = BlurHash.decode(
				blurHash = imageBlur ?: "",
				height = imageSizeInPx,
				width = imageSizeInPx,
			)
		}
	}

	GlideImage(
		model = imageUrl,
		contentDescription = null,
		modifier = modifier.size(imageSize)
	) {
		it.let { _it ->
			if (imageBlurHash != null) _it.placeholder(imageBlurHash?.toDrawable(resources = resources))
			else _it
		}.transform(CenterCrop(), RoundedCorners(roundingRadius))
	}
}
