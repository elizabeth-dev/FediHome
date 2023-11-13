package sh.elizabeth.fedihome.ui.composable

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import sh.elizabeth.fedihome.mock.defaultProfile
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme
import kotlin.math.roundToInt

private val AVATAR_SIZE = 48.dp

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SlimProfileSummary(
	modifier: Modifier = Modifier,
	profile: Profile,
	onClick: () -> Unit,
) {
	val resources = LocalContext.current.resources
	val avatarSizeInPx =
		resources.displayMetrics.densityDpi.div(160f).times(AVATAR_SIZE.value).roundToInt()
	var avatarBlurHash by remember { mutableStateOf<Bitmap?>(null) }

	LaunchedEffect(profile.avatarBlur) {
		CoroutineScope(Dispatchers.IO).launch {
			avatarBlurHash = BlurHash.decode(
				blurHash = profile.avatarBlur ?: "",
				height = avatarSizeInPx,
				width = avatarSizeInPx,
			)
		}
	}

	Surface(
		color = Color.Transparent,
		contentColor = MaterialTheme.colorScheme.onSurface,
		onClick = onClick,
		modifier = Modifier.clip(MaterialTheme.shapes.extraSmall)
	) {
		Row(modifier = modifier.padding(end = 8.dp)) {
			GlideImage(
				model = profile.avatarUrl,
				contentDescription = null,
				modifier = Modifier.size(AVATAR_SIZE)
			) { _it ->
				let {
					if (avatarBlurHash != null) _it.placeholder(avatarBlurHash?.toDrawable(resources = resources))
					else _it
				}.transform(CenterCrop(), RoundedCorners(21))
			}
			Column(
				Modifier
					.align(Alignment.CenterVertically)
					.padding(start = 8.dp)
			) {
				TextWithEmoji(
					profile.name ?: "",
					emojis = profile.emojis,
					style = MaterialTheme.typography.titleMedium,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				Text(
					"@${profile.fullUsername}",
					style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
				) // TODO: Maybe show short username on local profiles?
			}
		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun SlimProfileSummaryPreview() {
	FediHomeTheme {
		SlimProfileSummary(profile = defaultProfile, onClick = {})
	}
}
