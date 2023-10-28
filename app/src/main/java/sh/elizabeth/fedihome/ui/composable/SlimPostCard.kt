package sh.elizabeth.fedihome.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.fedihome.mock.defaultPost
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun SlimPostCard(
	post: Post,
	onReply: (String) -> Unit,
	onVotePoll: (choices: List<Int>) -> Unit,
	navToPost: (postId: String) -> Unit,
	navToProfile: (profileId: String) -> Unit,
) { // TODO: Check if it's better to pass individual props
	Surface(
		modifier = Modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surface,
		contentColor = MaterialTheme.colorScheme.onSurface,
		onClick = { navToPost(post.id) },
	) {
		Column {
			Column(
				Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {// TODO: Adapt padding for WindowSizeClass https://m3.material.io/foundations/layout/applying-layout/medium

				if (post.repostedBy != null) TopDisclaimer(
					icon = Icons.Rounded.Repeat,
					iconDescription = "Repost",
					text = "Reposted by ${post.repostedBy.name}",
					emojis = post.repostedBy.emojis
				)

				SlimProfileSummary(profile = post.author, navToProfile = navToProfile)

				if (!post.cw.isNullOrBlank()) {
					Text(
						text = post.cw, style = MaterialTheme.typography.titleMedium
					)
					Divider()
				}

				if (!post.text.isNullOrBlank()) TextWithEmoji(
					post.text,
					emojis = post.emojis,
					style = MaterialTheme.typography.bodyLarge, // TODO: Maybe use a smaller font size like bodyMedium
					modifier = Modifier
				)

				if (post.poll != null) PollDisplay(poll = post.poll) { onVotePoll(it) }

				if (post.quote != null) PostPreview(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 4.dp),
					post = post.quote,
					navToPost = navToPost,
					navToProfile = navToProfile
				)
			}

			Row(
				Modifier.padding(
					start = 4.dp, end = 4.dp
				)
			) { // TODO: No padding in the bottom makes the buttons ripple touch the divider
				IconButton(onClick = { onReply(post.id) }) {
					Icon(
						Icons.Outlined.Message,
						contentDescription = "Reply",
						tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
					)
				}

				Spacer(modifier = Modifier.weight(1f))

				IconButton(onClick = { /*TODO*/ }) {
					Icon(
						Icons.Rounded.Repeat,
						contentDescription = "Repost",
						tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
					)
				}
				IconButton(onClick = { /*TODO*/ }) {
					Icon(
						Icons.Rounded.StarBorder,
						contentDescription = "Star",
						tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
					)
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
	FediHomeTheme {
		SlimPostCard(
			post = defaultPost,
			onReply = {},
			onVotePoll = { },
			navToPost = { },
			navToProfile = { })
	}
}
