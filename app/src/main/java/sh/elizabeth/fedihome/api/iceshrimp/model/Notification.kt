package sh.elizabeth.fedihome.api.iceshrimp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.util.InstantAsString
import sh.elizabeth.fedihome.model.NotificationType as DomainNotificationType

@Serializable
enum class NotificationType {
//	@SerialName("note")
//	NOTE,

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

	@SerialName("pollVote")
	POLL_VOTE,

	@SerialName("pollEnded")
	POLL_ENDED,

	@SerialName("receiveFollowRequest")
	FOLLOW_REQ,

	@SerialName("followRequestAccepted")
	FOLLOW_ACCEPTED,

	@SerialName("app")
	APP,

	@SerialName("groupInvited")
	GROUP_INVITED,
}

@Serializable
data class Notification(
	val id: String,
	val createdAt: InstantAsString,
	val isRead: Boolean,
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
)

fun Notification.toDomain(fetchedFromInstance: String, forAccount: String) = sh.elizabeth.fedihome.model.Notification(
	id = "$id@$fetchedFromInstance",
	forAccount = forAccount,
	createdAt = createdAt,
	type = type.toDomain(),
	post = note?.toDomain(fetchedFromInstance),
	reaction = reaction,
	profile = user?.toDomain(fetchedFromInstance)
)

fun NotificationType.toDomain() = when (this) {
	NotificationType.FOLLOW -> DomainNotificationType.FOLLOW
	NotificationType.FOLLOW_ACCEPTED -> DomainNotificationType.FOLLOW_ACCEPTED
	NotificationType.FOLLOW_REQ -> DomainNotificationType.FOLLOW_REQ
	NotificationType.MENTION, NotificationType.REPLY -> DomainNotificationType.MENTION
	NotificationType.POLL_ENDED -> DomainNotificationType.POLL_ENDED
	NotificationType.QUOTE -> DomainNotificationType.QUOTE
	NotificationType.REACTION -> DomainNotificationType.REACTION
	NotificationType.RENOTE -> DomainNotificationType.REPOST
	NotificationType.POLL_VOTE -> DomainNotificationType.POLL_VOTE
	else -> DomainNotificationType.UNKNOWN
}
