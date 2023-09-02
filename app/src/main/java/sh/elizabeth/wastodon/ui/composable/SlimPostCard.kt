package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.wastodon.R
import sh.elizabeth.wastodon.model.Post
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.ui.theme.WastodonTheme
import java.time.Instant

@Composable
fun SlimPostCard(post: Post) { // TODO: Check if it's better to pass individual props
	Surface(
		Modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surface,
		contentColor = MaterialTheme.colorScheme.onSurface
	) {
		Column(
			Modifier.padding(
				horizontal = 16.dp,
				vertical = 8.dp
			)
		) {// TODO: Adapt padding for WindowSizeClass https://m3.material.io/foundations/layout/applying-layout/medium
			Row(Modifier.padding(bottom = 8.dp)) {
				Image(
					painter = painterResource(id = R.drawable.ic_launcher_background),
					contentDescription = null,
					modifier = Modifier.padding(end = 8.dp)
						.fillMaxWidth(0.125f)
						.clip(RoundedCornerShape(4.dp))
				) // Avatar
				Column(Modifier.align(Alignment.CenterVertically)) {
					Text(
						post.author.name ?: "",
						style = MaterialTheme.typography.titleMedium,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					) // Name
					Text(
						"@${post.author.fullUsername}",
						style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
					) // TODO: Maybe show short username on local profiles?
				}
			}
			Text(post.text, style = MaterialTheme.typography.bodyMedium) // Content
		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun SlimPostCardPreview() {
	WastodonTheme {
		SlimPostCard(
			Post(
				id = "foo",
				createdAt = Instant.now(),
				updatedAt = null,
				cw = null,
				text = "bar",
				author = Profile(
					id = "foo",
					username = "elizabeth",
					name = "Elizabeth",
					avatarUrl = null,
					instance = "blahaj.zone",
					fullUsername = "bar@blahaj.zone",
					headerUrl = null,
				)
			)
		)
	}
}
