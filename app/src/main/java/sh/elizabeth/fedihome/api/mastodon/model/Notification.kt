package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.util.InstantAsString
import sh.elizabeth.fedihome.util.containsEmoji
import sh.elizabeth.fedihome.model.Notification as DomainNotification
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

	@SerialName("reaction")
	REACTION,

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
	// From Iceshrimp.NET
	val emoji: String? = null,
	@SerialName("emoji_url") val emojiUrl: String? = null,
)

fun Notification.toDomain(fetchedFromInstance: String, forAccount: String): DomainNotification {
	val trimmedEmoji = emoji?.trim(':')
	val emojiContainsInstance = trimmedEmoji?.contains('@') ?: false

	return DomainNotification(
		id = "$id@$fetchedFromInstance",
		createdAt = createdAt,
		forAccount = forAccount,
		type = type.toDomain(),
		post = status?.toDomain(fetchedFromInstance),
		profile = account.toDomain(fetchedFromInstance),
		reaction = if (trimmedEmoji == null) null else if (emojiContainsInstance || trimmedEmoji.containsEmoji()) trimmedEmoji else "${trimmedEmoji}@$fetchedFromInstance",
		reactionEmoji = if (trimmedEmoji != null && emojiUrl != null) {
			sh.elizabeth.fedihome.model.Emoji(
				fullEmojiId = if (emojiContainsInstance) trimmedEmoji else "$trimmedEmoji@$fetchedFromInstance",
				instance = if (emojiContainsInstance) trimmedEmoji.split('@')
					.last() else fetchedFromInstance,
				shortcode = trimmedEmoji.split('@').first(),
				url = emojiUrl
			)
		} else {
			null
		}
	)
}

fun NotificationType.toDomain() = when (this) {
	NotificationType.STATUS -> DomainNotificationType.POST
	NotificationType.FOLLOW -> DomainNotificationType.FOLLOW
	NotificationType.FOLLOW_REQ -> DomainNotificationType.FOLLOW_REQ
	NotificationType.MENTION -> DomainNotificationType.MENTION
	NotificationType.POLL -> DomainNotificationType.POLL_ENDED
	NotificationType.FAVOURITE -> DomainNotificationType.FAVORITE
	NotificationType.REACTION -> DomainNotificationType.REACTION
	NotificationType.REBLOG -> DomainNotificationType.REPOST
	NotificationType.UPDATE -> DomainNotificationType.EDIT
}
