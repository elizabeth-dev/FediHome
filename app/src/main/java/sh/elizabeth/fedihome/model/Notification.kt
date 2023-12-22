package sh.elizabeth.fedihome.model

import java.time.Instant

data class Notification(
	val id: String, // id@instance
	val forAccount: String,
	val createdAt: Instant,
	val type: NotificationType,
	val profile: Profile?, // TODO: the notifications we support should always have a profile
	val post: Post?,
	val reaction: String?,
)

enum class NotificationType {
	POST,
	MENTION,
	REPOST,
	REACTION,
	FOLLOW,
	FOLLOW_REQ,
	POLL_VOTE,
	POLL_ENDED,
	EDIT,
	QUOTE,
	FOLLOW_ACCEPTED,
	UNKNOWN,
}
