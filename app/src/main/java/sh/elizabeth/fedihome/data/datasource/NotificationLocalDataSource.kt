package sh.elizabeth.fedihome.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedihome.NotificationEntity
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.database.entity.toDomain
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
						profileId = notification.profile?.id,
						postId = notification.post?.id
					)
				)
			}
		}

	fun getNotificationsFlow(forAccount: String): Flow<List<Notification>> =
		appDatabase.notificationQueries.getNotificationByAccount(forAccount)
			.asFlow()
			.mapToList(Dispatchers.IO)
			.map { notifications ->
				notifications.map { it.toDomain() }
			}

}
