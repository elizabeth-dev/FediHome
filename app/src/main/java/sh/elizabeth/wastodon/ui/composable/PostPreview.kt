package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.wastodon.model.Post
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.ui.theme.WastodonTheme
import java.time.Instant

@Composable
fun PostPreview(modifier: Modifier, post: Post) {
	Surface(
		modifier = modifier
			.border(
				1.dp,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
				shape = MaterialTheme.shapes.medium,
			),
		color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
		contentColor = MaterialTheme.colorScheme.onSurface,
		shape = MaterialTheme.shapes.medium,
	) {
		Column(Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
			SlimProfileSummary(modifier = Modifier.padding(bottom = 8.dp), profile = post.author)
			if (!post.text.isNullOrBlank()) Text(
				post.text,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(bottom = 0.dp)
			)
		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun PostPreviewPreview() {
	WastodonTheme {
		PostPreview(
			modifier = Modifier
				.padding(8.dp)
				.fillMaxWidth(), post = Post(
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
				repostedBy = null,
			)
		)
	}
}
