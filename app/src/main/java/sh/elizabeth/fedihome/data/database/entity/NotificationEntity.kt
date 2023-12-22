package sh.elizabeth.fedihome.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import sh.elizabeth.fedihome.model.NotificationType
import java.time.Instant

@Entity
data class NotificationEntity(

	@PrimaryKey(autoGenerate = true) var notificationRow: Long = 0,
	val notificationId: String, // $id@$fetchedFromInstance
	val forAccount: String,
	val createdAt: Instant,
	val type: NotificationType,
	val reaction: String?,
	val profileId: String?,
	val postId: String?
)
