package sh.elizabeth.fedihome.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.fedihome.mock.defaultProfile
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun SlimProfileSummary(
	modifier: Modifier = Modifier,
	profile: Profile,
	onClick: (() -> Unit)? = null,
) {
	Surface(
		color = Color.Transparent,
		contentColor = MaterialTheme.colorScheme.onSurface,
		modifier = Modifier
			.clip(MaterialTheme.shapes.extraSmall)
			.let {
				if (onClick != null) it
					.minimumInteractiveComponentSize()
					.clickable(onClick = onClick) else it
			}) {
		Row(modifier = modifier.padding(end = 8.dp)) {
			BlurHashAvatar(
				imageUrl = profile.avatarUrl,
				imageBlur = profile.avatarBlur,
			)

			Column(
				Modifier
					.align(Alignment.CenterVertically)
					.padding(start = 8.dp)
			) {
				EnrichedText(
					profile.name ?: "",
					emojis = profile.emojis,
					style = MaterialTheme.typography.titleMedium,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					allowClickable = false,
				)
				Text(
					"@${profile.username}",
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
		SlimProfileSummary(profile = defaultProfile) {}
	}
}
