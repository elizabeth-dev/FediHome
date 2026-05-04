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
						reactionEmoji = notification.reactionEmoji?.fullEmojiId,
						profileId = notification.profile?.id,
						postId = notification.post?.id,
					)
				)
			}
		}

	fun getNotificationsFlow(forAccount: String): Flow<List<Notification>> =
		appDatabase.notificationQueries
			.getNotificationByAccount(forAccount)
			.asFlow()
			.mapToList(Dispatchers.IO)
			.map { notifications ->
				val postIds = notifications.flatMap { setOf(it.postId, it.postId_, it.postId__) }
				val profileIds =
					notifications.flatMap {
						setOf(
							it.profileId,
							it.profileId_,
							it.profileId__,
							it.profileId___
						)
					}

				// TODO: see if we can incorporate emojis in main query
				val postEmojis = appDatabase.postQueries.getEmojisForPosts(postIds).executeAsList()
				val profileEmojis =
					appDatabase.profileQueries.getEmojisForProfiles(profileIds).executeAsList()
				notifications.map {
					it.toDomain(
						postEmojis.filter { postEmoji -> postEmoji.postId == it.postId || postEmoji.postId == it.postId__ },
						profileEmojis.filter { profileEmoji -> profileEmoji.profileId == it.profileId || profileEmoji.profileId == it.authorId || profileEmoji.profileId == it.authorId_ })
				}
			}

}
