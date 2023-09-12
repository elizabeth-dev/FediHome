package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sh.elizabeth.wastodon.model.Emoji
import sh.elizabeth.wastodon.ui.theme.WastodonTheme

@Composable
fun TopDisclaimer(
	modifier: Modifier = Modifier,
	icon: ImageVector,
	iconDescription: String,
	text: String,
	emojis: Map<String, Emoji> = emptyMap(),
) {
	Surface(
		modifier = Modifier.fillMaxWidth(),
		contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
	) {
		Row(
			modifier = modifier,
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				icon,
				contentDescription = iconDescription,
				modifier = Modifier.size(20.dp)
			)
			TextWithEmoji(
				text = text,
				emojis = emojis,
				emojiSize = 21.sp,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
			)
		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun TopDisclaimerPreview() {
	WastodonTheme {
		TopDisclaimer(
			icon = Icons.Rounded.Repeat,
			iconDescription = "Repost",
			text = "Reposted by Elizabeth"
		)
	}
}
