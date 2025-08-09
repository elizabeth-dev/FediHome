package sh.elizabeth.fedihome.ui.composable

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
import androidx.compose.ui.unit.sp
import sh.elizabeth.fedihome.localNavToProfile
import sh.elizabeth.fedihome.mock.defaultPost
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun PostPreview(
	modifier: Modifier = Modifier,
	post: Post,
) {
	val navToProfile = localNavToProfile.current
	val navToPost = localNavToProfile.current

	Surface(
		modifier = modifier.border(
			1.dp,
			color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
			shape = MaterialTheme.shapes.medium,
		),
		// FIXME
		color = MaterialTheme.colorScheme.primary.copy(
			alpha = 0.05f
		),
		contentColor = MaterialTheme.colorScheme.onSurface,
		shape = MaterialTheme.shapes.medium,
		onClick = { navToPost(post.id) },
	) {
		Column(
			Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			SlimProfileSummary(
				profile = post.author, onClick = { navToProfile(post.author.username) })
			if (!post.text.isNullOrBlank()) EnrichedText(
				text = post.text,
				emojis = post.emojis,
				emojiSize = 21.sp,
				style = MaterialTheme.typography.bodyMedium,
				instance = post.author.instance
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
					enabled = false,
					disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(
						alpha = 0.4f
					),
				),
			)
		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun PostPreviewPreview() {
	FediHomeTheme {
		PostPreview(
			modifier = Modifier
				.padding(8.dp)
				.fillMaxWidth(),
			post = defaultPost,
		)
	}
}
