package sh.elizabeth.fedihome.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import sh.elizabeth.fedihome.mock.followNotification
import sh.elizabeth.fedihome.mock.mentionNotification
import sh.elizabeth.fedihome.mock.reactionNotification
import sh.elizabeth.fedihome.model.Notification
import sh.elizabeth.fedihome.model.NotificationType
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun EmbeddedNotificationContext(notification: Notification) {
	val text = when (notification.type) {
		NotificationType.REACTION -> "${notification.profile?.name} reacted to your post"
		NotificationType.FAVORITE -> "${notification.profile?.name} favorited your post"
		NotificationType.REPOST -> "${notification.profile?.name} reposted your post"
		NotificationType.FOLLOW -> "${notification.profile?.name} followed you"
		NotificationType.FOLLOW_REQ -> "${notification.profile?.name} requested to follow you"
		NotificationType.POLL_VOTE -> "${notification.profile?.name} voted in your poll"
		NotificationType.POLL_ENDED -> "See the results of this poll"
		NotificationType.EDIT -> "${notification.profile?.name} edited"
		NotificationType.FOLLOW_ACCEPTED -> "${notification.profile?.name} accepted your follow request"
		else -> ""
	}

	val icon = when (notification.type) {
		NotificationType.REACTION, NotificationType.FAVORITE -> Icons.Rounded.Star
		NotificationType.REPOST -> Icons.Rounded.Repeat
		NotificationType.FOLLOW -> Icons.Rounded.PersonAdd
		NotificationType.FOLLOW_REQ -> Icons.Rounded.PersonOutline
		NotificationType.POLL_VOTE -> Icons.Outlined.Poll
		NotificationType.POLL_ENDED -> Icons.Outlined.Poll
		NotificationType.EDIT -> Icons.Rounded.Edit
		NotificationType.FOLLOW_ACCEPTED -> Icons.Rounded.PersonAdd
		else -> Icons.Rounded.Notifications
	}

	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
	) {
		if (notification.reactionEmoji != null) {
			AsyncImage(
				model = notification.reactionEmoji.url,
				contentDescription = notification.reactionEmoji.shortcode,
				modifier = Modifier
					.size(32.dp)
					.padding(end = 8.dp)
			)
		} else if (!notification.reaction.isNullOrBlank()) {
			Text(
				text = notification.reaction, fontSize = 18.sp, // TODO: harmonize icon sizes
				modifier = Modifier.padding(end = 8.dp)
			)
		} else {
			Icon(
				imageVector = icon,
				contentDescription = "foo",
				modifier = Modifier
					.padding(end = 8.dp)
					.size(24.dp)
			)
		}
		if (notification.type != NotificationType.POLL_ENDED && notification.type != NotificationType.POLL_VOTE) BlurHashAvatar(
			modifier = Modifier.padding(end = 8.dp),
			imageUrl = notification.profile?.avatarUrl,
			imageBlur = notification.profile?.avatarBlur,
			imageSize = 24.dp,
			roundingRadius = 8f
		)
		EnrichedText(
			text = text,
			style = MaterialTheme.typography.bodyLarge,
			emojis = notification.profile?.emojis ?: emptyMap(),
			instance = notification.profile?.instance ?: "",
		)
	}
}

@Composable
fun NotificationCard(
	notification: Notification,
	onVotePoll: (choices: List<Int>) -> Unit,
	onAddFavorite: (postId: String) -> Unit = {},
	onRemoveReaction: (postId: String) -> Unit = {},
	onAddReaction: (postId: String, reaction: String) -> Unit,
) {
	val isEmbeddedPost = arrayOf(
		NotificationType.REACTION,
		NotificationType.FAVORITE,
		NotificationType.REPOST,
		NotificationType.EDIT,
		NotificationType.POLL_ENDED,
		NotificationType.POLL_VOTE
	).contains(notification.type)

	Surface(
		modifier = Modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surface,
		contentColor = MaterialTheme.colorScheme.onSurface
	) {
		if (isEmbeddedPost) Column(
			modifier = Modifier.padding(
				vertical = 16.dp, horizontal = 12.dp
			)
		) {
			EmbeddedNotificationContext(notification)
			PostPreview(
				modifier = Modifier.fillMaxWidth(),
				post = notification.post!!,
			)
		} else if (notification.post != null) {
			SlimPostCard(
				post = notification.post,
				onVotePoll = onVotePoll,
				showDivider = false,
				onAddFavorite = onAddFavorite,
				onRemoveReaction = onRemoveReaction,
				onAddReaction = onAddReaction
			)
		} else if (notification.profile != null) {
			EmbeddedNotificationContext(notification = notification)
			ProfilePreview(
				modifier = Modifier.padding(
					vertical = 16.dp, horizontal = 12.dp
				), profile = notification.profile
			)
		}

	}
	HorizontalDivider(thickness = 1.dp)
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun NotificationCardMentionPreview() {
	FediHomeTheme {
		NotificationCard(mentionNotification, {}, {}, {}, { _, _ -> })
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun NotificationCardReactionPreview() {
	FediHomeTheme {
		NotificationCard(reactionNotification, {}, {}, {}, { _, _ -> })
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun NotificationCardFollowPreview() {
	FediHomeTheme {
		NotificationCard(followNotification, {}, {}, {}, { _, _ -> })
	}
}
