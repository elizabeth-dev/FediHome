package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
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
		Modifier
			.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surface,
		contentColor = MaterialTheme.colorScheme.onSurface
	) {
		Column {
			Column(
				Modifier.padding(
					start = 16.dp,
					end = 16.dp,
					top = 12.dp
				)
			) {// TODO: Adapt padding for WindowSizeClass https://m3.material.io/foundations/layout/applying-layout/medium
				Row(Modifier.padding(bottom = 8.dp)) {
					GlideImage(
						model = post.author.avatarUrl,
						contentDescription = null,
						modifier = Modifier
							.padding(end = 8.dp)
							.fillMaxWidth(0.125f)
							.clip(RoundedCornerShape(8.dp)),

						)
					Column(Modifier.align(Alignment.CenterVertically)) {
						Text(
							post.author.name ?: "",
							style = MaterialTheme.typography.titleMedium,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis
						)
						Text(
							"@${post.author.fullUsername}",
							style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
							color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
						) // TODO: Maybe show short username on local profiles?
					}
				}
				Text(
					post.text,
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.padding(bottom = 8.dp)
				)
				// Divider(thickness = Dp.Hairline)
			}
			Row(
				Modifier.padding(
					start = 4.dp,
					end = 4.dp
				)
			) { // TODO: No padding in the bottom makes the buttons ripple touch the divider
				IconButton(onClick = { onReply(post.id) }) {
					Icon(Icons.Outlined.Message, contentDescription = "Reply")
				}
				Spacer(modifier = Modifier.weight(1f))
				IconButton(onClick = { /*TODO*/ }) {
					Icon(Icons.Rounded.Repeat, contentDescription = "Reply")
				}
				IconButton(onClick = { /*TODO*/ }) {
					Icon(Icons.Rounded.StarBorder, contentDescription = "Reply")
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
		SlimPostCard(
			post = Post(
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
				)
			),
			onReply = {}
		)
	}
}
