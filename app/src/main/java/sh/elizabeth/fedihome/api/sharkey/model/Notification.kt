package sh.elizabeth.fedihome.api.sharkey.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.util.InstantAsString
import sh.elizabeth.fedihome.model.NotificationType as DomainNotificationType

@Serializable
enum class NotificationType {
	@SerialName("note")
	NOTE,

	@SerialName("follow")
	FOLLOW,

	@SerialName("mention")
	MENTION,

	@SerialName("reply")
	REPLY,

	@SerialName("renote")
	RENOTE,

	@SerialName("quote")
	QUOTE,

	@SerialName("reaction")
	REACTION,

	@SerialName("pollEnded")
	POLL_ENDED,

	@SerialName("receiveFollowRequest")
	FOLLOW_REQ,

	@SerialName("followRequestAccepted")
	FOLLOW_ACCEPTED,

	@SerialName("achievementEarned")
	ACHIEVEMENT,

	@SerialName("app")
	APP,

	@SerialName("test")
	TEST,

	@SerialName("reaction:grouped")
	REACTIONS_GROUPED,

	@SerialName("renote:grouped")
	RENOTE_GROUPED
}

@Serializable
data class Notification(
	val id: String,
	val createdAt: InstantAsString,
	// FIXME: filter notificaion types requested
	val type: NotificationType,
	val user: UserLite?,
	val userId: String? = null,
	val note: Post?,
	val reaction: String? = null,
	val choice: Int? = null,
//	val invitation: Any, // TODO: investigate what this field is
	val body: String? = null,
	val header: String? = null,
	val icon: String? = null,
	val achievement: String? = null,
)

fun Notification.toDomain(fetchedFromInstance: String, forAccount: String) = sh.elizabeth.fedihome.model.Notification(
	id = "$id@$fetchedFromInstance",
	createdAt = createdAt,
	forAccount = forAccount,
	type = type.toDomain(),
	post = note?.toDomain(fetchedFromInstance),
	reaction = reaction,
	profile = user?.toDomain(fetchedFromInstance)
)

fun NotificationType.toDomain() = when (this) {
	NotificationType.NOTE -> DomainNotificationType.POST
	NotificationType.FOLLOW -> DomainNotificationType.FOLLOW
	NotificationType.FOLLOW_ACCEPTED -> DomainNotificationType.FOLLOW_ACCEPTED
	NotificationType.FOLLOW_REQ -> DomainNotificationType.FOLLOW_REQ
	NotificationType.MENTION, NotificationType.REPLY -> DomainNotificationType.MENTION
	NotificationType.POLL_ENDED -> DomainNotificationType.POLL_ENDED
	NotificationType.QUOTE -> DomainNotificationType.QUOTE
	NotificationType.REACTION -> DomainNotificationType.REACTION
	NotificationType.RENOTE -> DomainNotificationType.REPOST
	else -> DomainNotificationType.UNKNOWN
}
