package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.NotificationEntity
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.model.Notification
import sh.elizabeth.fedihome.model.NotificationPagingItemType
import javax.inject.Inject

class NotificationLocalDataSource @Inject constructor(private val appDatabase: AppDatabase) {

	fun insertOrReplace(vararg notifications: Notification) =
		notifications.forEach { notification ->
			appDatabase.notificationQueries.insert(
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

	fun insertNotificationPagingItems(vararg notifications: Notification) =
		notifications.forEach { notification ->
			appDatabase.notificationQueries.insertPagingItem(
				forAccount = notification.forAccount,
				notificationId = notification.id,
				type = NotificationPagingItemType.NOTIFICATION
			)
		}

//	fun getNotificationPagingSource(forAccount: String) = QueryPagingSource(
//		transacter = appDatabase,
//		context = Dispatchers.IO,
//		pageBoundariesProvider = { anchor, limit ->
//			appDatabase.notificationQueries.pageBoundaries(
//				limit = limit, anchor = anchor, forAccount = forAccount
//			)
//		},
//		queryProvider = { beginInclusive: String, endExclusive: String? ->
//			appDatabase.notificationQueries.getNotificationsByAccount(
//				forAccount = forAccount,
//				beginInclusive = beginInclusive,
//				endExclusive = endExclusive
//			)
//		})

}
