package sh.elizabeth.fedihome.ui.composable

import android.content.res.Configuration
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import sh.elizabeth.fedihome.mock.followNotification
import sh.elizabeth.fedihome.mock.mentionNotification
import sh.elizabeth.fedihome.mock.reactionNotification
import sh.elizabeth.fedihome.model.Notification
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun NotificationCard(notification: Notification) {
	Surface {

	}
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
