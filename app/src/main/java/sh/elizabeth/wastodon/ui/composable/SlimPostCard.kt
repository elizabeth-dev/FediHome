package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import sh.elizabeth.wastodon.model.Post
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.ui.theme.WastodonTheme
import java.time.Instant

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SlimPostCard(
	post: Post,
	onReply: (String) -> Unit,
) { // TODO: Check if it's better to pass individual props
	Surface(
		Modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surface,
		contentColor = MaterialTheme.colorScheme.onSurface
	) {
		Column {
			Column(
				Modifier.padding(
					start = 16.dp, end = 16.dp, top = 12.dp
				)
			) {// TODO: Adapt padding for WindowSizeClass https://m3.material.io/foundations/layout/applying-layout/medium
				if (post.repostedBy != null) Surface(
					Modifier
						.padding(bottom = 8.dp),
					contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
				) {
					Row(
						horizontalArrangement = Arrangement.spacedBy(
							8.dp, Alignment.Start
						),
						verticalAlignment = Alignment.CenterVertically
					) {
						Icon(
							Icons.Rounded.Repeat,
							contentDescription = "Repost",
							modifier = Modifier.size(20.dp)
						)
						Text(
							"Reposted by ${post.repostedBy.name}",
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
						)
					}
				}
				SlimProfileSummary(
					modifier = Modifier.padding(bottom = 8.dp),
					profile = post.author
				)
				if (!post.text.isNullOrBlank()) Text(
					post.text,
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.padding(bottom = 8.dp)
				)
				if (post.quote != null) PostPreview(
					modifier = Modifier.fillMaxWidth(),
					post = post.quote
				)
			}
			Row(
				Modifier.padding(
					start = 4.dp, end = 4.dp
				)
			) { // TODO: No padding in the bottom makes the buttons ripple touch the divider
				IconButton(onClick = { onReply(post.id) }) {
					Icon(Icons.Outlined.Message, contentDescription = "Reply")
				}
				Spacer(modifier = Modifier.weight(1f))
				IconButton(onClick = { /*TODO*/ }) {
					Icon(Icons.Rounded.Repeat, contentDescription = "Repost")
				}
				IconButton(onClick = { /*TODO*/ }) {
					Icon(Icons.Rounded.StarBorder, contentDescription = "Star")
				}

			}
			Divider(thickness = 1.dp)
		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun SlimPostCardPreview() {
	WastodonTheme {
		SlimPostCard(post = Post(
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
				fullUsername = "elizabeth@blahaj.zone",
				headerUrl = null,

				),
			quote = Post(
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
					fullUsername = "elizabeth@blahaj.zone",
					headerUrl = null,

					),
				quote = null,
				repostedBy = null
			),
			repostedBy = Profile(
				id = "foo",
				username = "elizabeth",
				name = "Elizabeth",
				avatarUrl = null,
				instance = "blahaj.zone",
				fullUsername = "elizabeth@blahaj.zone",
				headerUrl = null,
			),
		), onReply = {})
	}
}
