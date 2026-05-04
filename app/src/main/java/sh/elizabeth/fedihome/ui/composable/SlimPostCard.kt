package sh.elizabeth.fedihome.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.fedihome.localNavToCompose
import sh.elizabeth.fedihome.localNavToPost
import sh.elizabeth.fedihome.localNavToProfile
import sh.elizabeth.fedihome.mock.defaultPost
import sh.elizabeth.fedihome.model.Attachment
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddBoost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddFavorite
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddReaction
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveBoost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveFavorite
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveReaction
import sh.elizabeth.fedihome.ui.compositionlocals.localOnVotePoll
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun SlimPostCard(
	post: Post,
	showDivider: Boolean = true,
	disablePostClick: Boolean = false,
) {
	val navToPost = localNavToPost.current
	val navToProfile = localNavToProfile.current
	val onReply = localNavToCompose.current
	val onVotePoll = localOnVotePoll.current
	val onAddFavorite = localOnAddFavorite.current
	val onRemoveFavorite = localOnRemoveFavorite.current
	val onRemoveReaction = localOnRemoveReaction.current
	val onAddReaction = localOnAddReaction.current
	val onBoost = localOnAddBoost.current
	val onUnboost = localOnRemoveBoost.current

	Surface(
		modifier = Modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surface,
		contentColor = MaterialTheme.colorScheme.onSurface,
		onClick = { if (!disablePostClick) navToPost(post.id) },
	) {
		Column {
			Column(
				Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {// TODO: Adapt padding for WindowSizeClass https://m3.material.io/foundations/layout/applying-layout/medium

				if (post.boostedBy != null) TopDisclaimer(
					icon = Icons.Rounded.Repeat,
					iconDescription = "Repost",
					text = "Reposted by ${post.boostedBy.name}",
					emojis = post.boostedBy.emojis
				)

				SlimProfileSummary(
					profile = post.author, onClick = { navToProfile(post.author.username) })

				if (!post.cw.isNullOrBlank()) {
					Text(
						text = post.cw, style = MaterialTheme.typography.titleMedium
					)
					HorizontalDivider()
				}

				if (!post.text.isNullOrBlank()) EnrichedText(
					post.text,
					emojis = post.emojis,
					mentionLinksMap = post.mentionLinksMap,
					style = MaterialTheme.typography.bodyLarge, // TODO: Maybe use a smaller font size like bodyMedium
					modifier = Modifier,
					instance = post.author.instance
				)

				if (post.attachments.isNotEmpty()) AttachmentGrid(attachments = post.attachments)

				if (post.poll != null) PollDisplay(poll = post.poll) {
					onVotePoll(post.id, post.poll.id, it)
				}

				if (post.quote != null) PostPreview(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 4.dp),
					post = post.quote,
				)

				if (post.reactions.isNotEmpty()) Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(4.dp)
				) {
					for (reaction in post.reactions) {
						ReactionButton(
							icon = reaction.key,
							count = reaction.value,
							selected = post.myReactions.contains(reaction.key),
							emojis = post.emojis,
						)
					}
				}
			}

			Row(
				Modifier.padding(
					horizontal = 8.dp, vertical = 8.dp
				),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(4.dp)
			) { // TODO: No padding in the bottom makes the buttons ripple touch the divider
				InteractionButton(
					icons = InteractionButtonIcons(
						unSelectedIcon = Icons.AutoMirrored.Outlined.Message,
						unSelectedIconDescription = "Reply"
					), onClick = { onReply(post.id) }, onClickLabel = "Reply"
				)

				Spacer(modifier = Modifier.weight(1f))

				InteractionButton(
					count = post.boosts,
					selected = post.boosted,
					icons = InteractionButtonIcons(
						selectedIcon = Icons.Rounded.Repeat,
						selectedIconDescription = "Boosted",
						unSelectedIcon = Icons.Rounded.Repeat,
						unSelectedIconDescription = "Boost"
					),
					onClickLabel = if (!post.boosted) "Boost" else "Unboost",
					onClick = { if (!post.boosted) onBoost(post.id) else onUnboost(post.id) }

				)
				InteractionButton(
					// Fav button
					count = post.favorites,
					selected = post.favorited,
					icons = InteractionButtonIcons(
						selectedIcon = Icons.Rounded.Star,
						selectedIconDescription = "Favorited",
						unSelectedIcon = Icons.Rounded.StarBorder,
						unSelectedIconDescription = "Favorite"
					),
					onClickLabel = if (!post.favorited) "Favorite" else "Unfavorite",
					onClick = {
						if (!post.favorited) onAddFavorite(post.id) else onRemoveFavorite(
							post.id
						)
					})
			}
			if (showDivider) HorizontalDivider(thickness = 1.dp)
		}
	}
}

@Composable
private fun AttachmentGrid(attachments: List<Attachment>) {
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		attachments.forEach {
			BlurHashImage(
				imageUrl = it.url,
				imageBlur = it.blurhash,
			)
		}
	}
}

@Preview(
	uiMode = Configuration.UI_MODE_NIGHT_YES,
	showBackground = true,
	name = "Dark",
	group = "SlimPostCard"
)
@Preview(showBackground = true, name = "Light", group = "SlimPostCard")
@Composable
fun SlimPostCardPreview() {
	FediHomeTheme {
		SlimPostCard(
			post = defaultPost,
		)
	}
}
