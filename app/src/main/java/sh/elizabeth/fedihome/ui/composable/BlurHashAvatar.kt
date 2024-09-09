package sh.elizabeth.fedihome.ui.composable

// import getValue and setValue from androidx.compose.runtime
import android.graphics.Bitmap
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.vanniktech.blurhash.BlurHash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun BlurHashAvatar(
	modifier: Modifier = Modifier,
	imageUrl: String?,
	imageBlur: String?,
	imageSize: Dp = 48.dp,
	roundingRadius: Float = 16f,
) {
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

	AsyncImage(
		model = imageUrl,
		contentDescription = null,
		modifier = modifier
			.size(imageSize)
			.clip(RoundedCornerShape(roundingRadius)),
		contentScale = ContentScale.Crop,
		placeholder = imageBlurHash?.let {
			rememberAsyncImagePainter(
				model = it, contentScale =
				ContentScale.Crop
			)
		}
	)
}
