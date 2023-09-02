package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
	Column(Modifier.padding(8.dp).fillMaxWidth()) {
		Row(Modifier.padding(bottom = 8.dp)) {
			Image(
				painter = painterResource(id = R.drawable.ic_launcher_background),
				contentDescription = null,
				modifier = Modifier.padding(end = 8.dp).fillMaxWidth(0.125f) // FIXME: Check correct Material ratio
			) // Avatar
			Column(Modifier.align(Alignment.CenterVertically)) {
				Text(
					post.author.name ?: "",
					style = MaterialTheme.typography.titleMedium,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				) // Name
				Text(
					"@${post.author.username}",
					style = MaterialTheme.typography.titleSmall,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					color = MaterialTheme.typography.titleSmall.color.copy(alpha = 0.6f)
				) // User
			}
		}
		Text(post.text, style = MaterialTheme.typography.bodyLarge) // Content
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
					username = "bar",
					name = "baz",
					avatarUrl = null,
					instance = "blahaj.zone",
					fullUsername = "bar@blahaj.zone",
					headerUrl = null,
				)
			)
		)
	}
}
