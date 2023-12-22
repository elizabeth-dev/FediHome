package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.util.InstantAsString
import sh.elizabeth.fedihome.model.NotificationType as DomainNotificationType

@Serializable
enum class NotificationType {
	@SerialName("status")
	STATUS,

	@SerialName("follow")
	FOLLOW,

	@SerialName("mention")
	MENTION,

	@SerialName("reblog")
	REBLOG,

	@SerialName("favourite")
	FAVOURITE,

	@SerialName("poll")
	POLL,

	@SerialName("follow_request")
	FOLLOW_REQ,

	@SerialName("update")
	UPDATE
}

@Serializable
data class Notification(
	val id: String,
	@SerialName("created_at") val createdAt: InstantAsString,
	val type: NotificationType,
	val account: Profile,
	val status: Post?,
)

fun Notification.toDomain(fetchedFromInstance: String, forAccount: String) = sh.elizabeth.fedihome.model.Notification(
	id = "$id@$fetchedFromInstance",
	createdAt = createdAt,
	forAccount = forAccount,
	type = type.toDomain(),
	post = status?.toDomain(fetchedFromInstance),
	reaction = "⭐", // FIXME: put correct reaction emoji here
	profile = account.toDomain(fetchedFromInstance)
)

fun NotificationType.toDomain() = when (this) {
	NotificationType.STATUS -> DomainNotificationType.POST
	NotificationType.FOLLOW -> DomainNotificationType.FOLLOW
	NotificationType.FOLLOW_REQ -> DomainNotificationType.FOLLOW_REQ
	NotificationType.MENTION -> DomainNotificationType.MENTION
	NotificationType.POLL -> DomainNotificationType.POLL_ENDED
	NotificationType.FAVOURITE -> DomainNotificationType.REACTION
	NotificationType.REBLOG -> DomainNotificationType.REPOST
	NotificationType.UPDATE -> DomainNotificationType.EDIT
}
