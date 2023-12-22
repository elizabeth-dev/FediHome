package sh.elizabeth.fedihome.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedihome.data.database.dao.NotificationDao
import sh.elizabeth.fedihome.data.database.entity.EnrichedNotification
import sh.elizabeth.fedihome.data.database.entity.NotificationEntity
import sh.elizabeth.fedihome.data.database.entity.toDomain
import sh.elizabeth.fedihome.model.Notification
import javax.inject.Inject

class NotificationLocalDataSource @Inject constructor(private val notificationDao: NotificationDao) {
	suspend fun insertOrReplace(vararg notifications: Notification): List<Long> {
		return notificationDao.insertOrReplace(
			*notifications.map(Notification::toEntity).toTypedArray()
		)
	}

	fun getNotificationsFlow(forAccount: String): Flow<List<Notification>> =
		notificationDao.getNotificationsFlow(forAccount)
			.map { it.map(EnrichedNotification::toDomain) }

}

fun Notification.toEntity() = NotificationEntity(
	notificationId = id,
	forAccount = forAccount,
	createdAt = createdAt,
	type = type,
	reaction = reaction,
	profileId = profile?.id,
	postId = post?.id
)
