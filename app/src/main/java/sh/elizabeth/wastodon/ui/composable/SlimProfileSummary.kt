package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.vanniktech.blurhash.BlurHash
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.ui.theme.WastodonTheme
import kotlin.math.roundToInt

val AVATAR_SIZE = 48.dp

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SlimProfileSummary(modifier: Modifier = Modifier, profile: Profile) {
	val resources = LocalContext.current.resources
	val avatarSizeInPx =
		resources.displayMetrics.densityDpi.div(160f).times(AVATAR_SIZE.value).roundToInt()
	Row(modifier = modifier) {
		GlideImage(
			model = profile.avatarUrl,
			contentDescription = null,
			modifier = Modifier
				.padding(end = 8.dp)
				.size(AVATAR_SIZE)
				.clip(RoundedCornerShape(8.dp)),
		) {
			val bitmap = BlurHash.decode(
				blurHash = profile.avatarBlur ?: "", height = avatarSizeInPx, width = avatarSizeInPx
			)?.toDrawable(resources = resources)
			it.placeholder(bitmap)
		}
		Column(Modifier.align(Alignment.CenterVertically)) {
			Text(
				profile.name ?: "",
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun SlimProfileSummaryPreview() {
	WastodonTheme {
		SlimProfileSummary(
			modifier = Modifier, profile = Profile(
				id = "foo",
				username = "elizabeth",
				name = "Elizabeth",
				avatarUrl = null,
				avatarBlur = null,
				instance = "blahaj.zone",
				fullUsername = "bar@blahaj.zone",
				headerUrl = null,
			)

		)
	}
}
