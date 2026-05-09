package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.NotificationEntity
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.model.Notification
import javax.inject.Inject

class NotificationLocalDataSource @Inject constructor(private val appDatabase: AppDatabase) {
	fun insertOrReplace(vararg notifications: Notification) =
		appDatabase.notificationQueries.transaction {
			notifications.forEach { notification ->
				appDatabase.notificationQueries.insertOrReplace(
					NotificationEntity(
						notificationId = notification.id,
						forAccount = notification.forAccount,
						createdAt = notification.createdAt,
						type = notification.type,
						reaction = notification.reaction,
						reactionEmoji = notification.reactionEmoji?.fullEmojiId,
						profileId = notification.profile?.id,
						postId = notification.post?.id,
					)
				)
			}
		}

}
