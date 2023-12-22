package sh.elizabeth.fedihome.data.database.entity

import androidx.room.Embedded
import sh.elizabeth.fedihome.model.Notification

class EnrichedNotification(
	@Embedded val notification: NotificationEntity,

	@Embedded(prefix = "profile_") val profile: FullProfile,

	@Embedded(prefix = "post_") val post: EnrichedPost?,
)

fun EnrichedNotification.toDomain(): Notification = Notification(
	id = notification.notificationId,
	forAccount = notification.forAccount,
	createdAt = notification.createdAt,
	type = notification.type,
	profile = profile.toDomain(
		emptyList()
	),
	post = post?.toPostDomain(),
	reaction = notification.reaction
)
