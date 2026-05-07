package sh.elizabeth.fedihome.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import sh.elizabeth.fedihome.R
import sh.elizabeth.fedihome.mock.followNotification
import sh.elizabeth.fedihome.mock.mentionNotification
import sh.elizabeth.fedihome.mock.reactionNotification
import sh.elizabeth.fedihome.model.Notification
import sh.elizabeth.fedihome.model.NotificationType
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun EmbeddedNotificationContext(
	notification: Notification,
	content: @Composable (ColumnScope.() -> Unit)? = null,
) {
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
		NotificationType.QUOTE -> "${notification.profile?.name} quoted your post"
		NotificationType.BITE_BACK -> if (notification.post != null) "${notification.profile?.name} bit your post back" else "${notification.profile?.name} bit you back"
		NotificationType.BITE -> if (notification.post != null) "${notification.profile?.name} bit your post" else "${notification.profile?.name} bit you"
		else -> ""
	}

	val icon = when (notification.type) {
		NotificationType.REACTION, NotificationType.FAVORITE -> painterResource(R.drawable.icon_star)
		NotificationType.REPOST -> painterResource(R.drawable.icon_repeat)
		NotificationType.FOLLOW, NotificationType.FOLLOW_ACCEPTED -> painterResource(R.drawable.icon_person_add)
		NotificationType.FOLLOW_REQ -> painterResource(R.drawable.icon_outline_person)
		NotificationType.POLL_VOTE, NotificationType.POLL_ENDED -> painterResource(R.drawable.icon_outline_insert_chart)
		NotificationType.EDIT -> painterResource(R.drawable.icon_edit)
		NotificationType.QUOTE -> painterResource(R.drawable.icon_format_quote)
		NotificationType.BITE, NotificationType.BITE_BACK -> painterResource(R.drawable.icon_outline_dentistry)

		else -> painterResource(R.drawable.icon_notifications)
	}

	Column(
		modifier = Modifier.padding(
			vertical = 16.dp, horizontal = 12.dp
		), verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.padding(start = 8.dp)
		) {
			if (notification.type != NotificationType.POLL_ENDED && notification.type != NotificationType.POLL_VOTE) Box(
				modifier = Modifier.padding(end = 8.dp), contentAlignment = Alignment.BottomEnd
			) {
				BlurHashAvatar(
					modifier = Modifier.padding(end = 4.dp, bottom = 4.dp),
					imageUrl = notification.profile?.avatarUrl,
					imageBlur = notification.profile?.avatarBlur,
					imageSize = 32.dp,
					roundingRadius = 8f
				)

				if (notification.reactionEmoji != null) {
					AsyncImage(
						model = notification.reactionEmoji.url,
						contentDescription = notification.reactionEmoji.shortcode,
						modifier = Modifier.size(18.dp)
					)
				}
				else if (!notification.reaction.isNullOrBlank()) {
					Text(
						text = notification.reaction,
						color = MaterialTheme.colorScheme.onPrimaryContainer,
						fontSize = 14.sp, // TODO: harmonize icon sizes
						lineHeight = 14.sp
					)
				}
				else {
					Box(
						modifier = Modifier
							.clip(
								CircleShape
							)
							.background(MaterialTheme.colorScheme.primaryContainer)
							.padding(1.dp)
					) {
						Icon(
							painter = icon,
							contentDescription = "foo",
							tint = MaterialTheme.colorScheme.onPrimaryContainer,
							modifier = Modifier.size(16.dp)
						)
					}
				}
			}
			EnrichedText(
				text = text,
				style = MaterialTheme.typography.bodyLarge,
				emojis = notification.profile?.emojis ?: emptyMap(),
				instance = notification.profile?.instance ?: "",
				modifier = Modifier.padding(bottom = 4.dp)
			)
		}
		content?.invoke(this)
	}
}
//
//@Composable
//fun GenericNotificationContent() {
//
//}

@Composable
fun NotificationCard(
	notification: Notification,
) {
	val isEmbeddedPost = arrayOf(
		NotificationType.REACTION,
		NotificationType.FAVORITE,
		NotificationType.REPOST,
		NotificationType.EDIT,
		NotificationType.POLL_ENDED,
		NotificationType.POLL_VOTE,
	).contains(notification.type) || (notification.type == NotificationType.BITE && notification.post != null)

	Surface(
		modifier = Modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surface,
		contentColor = MaterialTheme.colorScheme.onSurface
	) {
		if (isEmbeddedPost) EmbeddedNotificationContext(notification) {
			PostPreview(
				modifier = Modifier.fillMaxWidth(),
				post = notification.post!!,
			)
		}
		else if (notification.type == NotificationType.BITE || notification.type == NotificationType.BITE_BACK) {
			EmbeddedNotificationContext(notification)
		}
		else if (notification.post != null) {
			SlimPostCard(
				post = notification.post,
				showDivider = false,
			)
		}
		else if (notification.profile != null) {

			EmbeddedNotificationContext(notification = notification) {
				ProfilePreview(
					modifier = Modifier.padding(
						vertical = 16.dp, horizontal = 12.dp
					), profile = notification.profile
				)
			}
		}

	}
	HorizontalDivider(thickness = 1.dp)
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun NotificationCardMentionPreview() {
	FediHomeTheme {
		NotificationCard(mentionNotification)
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun NotificationCardReactionPreview() {
	FediHomeTheme {
		NotificationCard(reactionNotification)
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun NotificationCardFollowPreview() {
	FediHomeTheme {
		NotificationCard(followNotification)
	}
}
