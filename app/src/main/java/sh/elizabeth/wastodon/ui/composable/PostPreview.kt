package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Poll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.wastodon.model.Poll
import sh.elizabeth.wastodon.model.PollChoice
import sh.elizabeth.wastodon.model.Post
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.ui.theme.WastodonTheme
import java.time.Instant

@Composable
fun PostPreview(modifier: Modifier, post: Post) {
	Surface(
		modifier = modifier.border(
			1.dp,
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
			shape = MaterialTheme.shapes.medium,
		),
		color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
		contentColor = MaterialTheme.colorScheme.onSurface,
		shape = MaterialTheme.shapes.medium,
	) {
		Column(
			Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			SlimProfileSummary(profile = post.author)
			if (!post.text.isNullOrBlank()) Text(
				post.text,
				style = MaterialTheme.typography.bodyMedium,
			)
			if (post.poll != null) AssistChip(
				onClick = {},
				enabled = false,
				label = { Text("Poll") },
				leadingIcon = { Icon(Icons.Rounded.Poll, contentDescription = "Poll icon") },
				colors = AssistChipDefaults.assistChipColors(
					disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
					disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
				),
				border = AssistChipDefaults.assistChipBorder(
					disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
				),
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
					avatarBlur = null,
					instance = "blahaj.zone",
					fullUsername = "elizabeth@blahaj.zone",
					headerUrl = null,

					),
				quote = null,
				repostedBy = null,
				poll = Poll(
					voted = false, expiresAt = null, multiple = false, choices = listOf(
						PollChoice(
							text = "foo", votes = 0, isVoted = false
						), PollChoice(
							text = "bar", votes = 0, isVoted = false
						)
					)
				),
			)
		)
	}
}
