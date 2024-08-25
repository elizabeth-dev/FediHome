package sh.elizabeth.fedihome.ui.routes.login.notifications

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.fedihome.model.NotificationType
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme
import sh.elizabeth.fedihome.util.SupportedInstances

val notificationTypesByInstanceType = mapOf(
	SupportedInstances.MASTODON to arrayOf(
		NotificationType.POST,
		NotificationType.FOLLOW,
		NotificationType.FOLLOW_REQ,
		NotificationType.MENTION,
		NotificationType.POLL_ENDED,
		NotificationType.REACTION,
		NotificationType.REPOST,
		NotificationType.EDIT
	),
	SupportedInstances.GLITCH to arrayOf(
		NotificationType.POST,
		NotificationType.FOLLOW,
		NotificationType.FOLLOW_REQ,
		NotificationType.MENTION,
		NotificationType.POLL_ENDED,
		NotificationType.REACTION,
		NotificationType.REPOST,
		NotificationType.EDIT
	),
	SupportedInstances.SHARKEY to arrayOf(),
	SupportedInstances.FIREFISH to arrayOf()
)

val notificationTypesLabels = mapOf(
	NotificationType.POST to "Subscribed profiles",
	NotificationType.FOLLOW to "New followers",
	NotificationType.FOLLOW_REQ to "Follow requests",
	NotificationType.MENTION to "Mentions",
	NotificationType.POLL_ENDED to "Ended polls",
	NotificationType.REACTION to "Reactions",
	NotificationType.REPOST to "Reposts",
	NotificationType.EDIT to "Edited posts",
	NotificationType.FOLLOW_ACCEPTED to "Accepted follows",
	NotificationType.QUOTE to "Quote posts",
	NotificationType.POLL_VOTE to "Poll votes"
)

enum class NotificationPolicy {
	ALL, FOLLOWED, FOLLOWER, NONE
}

@Composable
fun LoginNotificationsScreen(
	instanceType: SupportedInstances,
	onNext: (enable: Boolean, types: Map<NotificationType, Boolean>, policy: NotificationPolicy) -> Unit,
) {
	var enableNotifications by remember { mutableStateOf(true) }
	val notificationTypes = remember {
		mutableStateMapOf(*notificationTypesByInstanceType[instanceType]!!.map { it to true }
			.toTypedArray())
	} // FIXME: this does not hold a specific order

	val notificationPolicy by remember { mutableStateOf(NotificationPolicy.ALL) }

	Scaffold { paddingValues ->
		Column {
			Column(
				modifier = Modifier
					.padding(paddingValues)
					.padding(vertical = 16.dp)
					.fillMaxWidth()
					.weight(1f)
			) {
				Text(
					text = "Notifications",
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 12.dp),
					style = MaterialTheme.typography.displayMedium
				)

				Text(
					text = "All push notifications are end-to-end encrypted from your instance to your device, travelling through FediHome's relay and Firebase.",
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)

				Surface(
					modifier = Modifier
						.padding(
							vertical = 8.dp, horizontal = 16.dp
						)
						.fillMaxWidth(),
					shape = MaterialTheme.shapes.extraLarge,
					checked = enableNotifications,
					onCheckedChange = { enableNotifications = it },
					color = if (enableNotifications) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
				) {

					Row(
						modifier = Modifier.padding(
							horizontal = 20.dp, vertical = 16.dp
						),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text(

							text = "Push notifications",
							textAlign = TextAlign.Start,
							style = MaterialTheme.typography.titleLarge
						)
						Switch(checked = enableNotifications,
							onCheckedChange = {})
					}
				}

				if (enableNotifications) notificationTypes.forEach { notificationType ->
					val (type, enabled) = notificationType
					Surface(
						modifier = Modifier.fillMaxWidth(),
						checked = enabled,
						onCheckedChange = { notificationTypes[type] = it },
					) {
						Row(
							modifier = Modifier.padding(
								horizontal = 36.dp, vertical = 12.dp
							),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.SpaceBetween
						) {
							Text(
								text = notificationTypesLabels[type]!!,
								textAlign = TextAlign.Start,
								style = MaterialTheme.typography.titleLarge
							)
							Switch(checked = enabled, onCheckedChange = {})
						}
					}
				}
			}
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				horizontalArrangement = Arrangement.End
			) {
				Button(onClick = {
					onNext(
						enableNotifications,
						notificationTypes,
						notificationPolicy
					)
				}) {
					Text(
						"Next", style = MaterialTheme.typography.labelLarge
					)
				}
			}
		}
	}
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun LoginNotificationsPreview() {
	FediHomeTheme {
		LoginNotificationsScreen(
			instanceType = SupportedInstances.MASTODON,
			{ _, _, _ -> })
	}
}