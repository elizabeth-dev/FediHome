package sh.elizabeth.fedihome.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler

@OptIn(ExperimentalCoilApi::class)
val previewHandler = AsyncImagePreviewHandler {
	ColorImage(Color.Blue.toArgb())
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PreviewImages(content: @Composable (() -> Unit)) {
	CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
		content()
	}
}