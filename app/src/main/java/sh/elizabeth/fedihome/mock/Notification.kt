package sh.elizabeth.fedihome.mock

import sh.elizabeth.fedihome.model.Emoji
import sh.elizabeth.fedihome.model.Notification
import sh.elizabeth.fedihome.model.NotificationType
import java.time.Instant

val mentionNotification = Notification(
	id = "foo",
	forAccount = "foo-bar@tech.lgbt",
	createdAt = Instant.now(),
	type = NotificationType.MENTION,
	profile = defaultProfile,
	post = defaultPost,
	reaction = null,
	reactionEmoji = null
)

val reactionNotification = Notification(
	id = "foo",
	forAccount = "foo-bar@tech.lgbt",
	createdAt = Instant.now(),
	type = NotificationType.REACTION,
	profile = defaultProfile,
	post = defaultPost,
	reaction = "foo@tech.lgbt",
	reactionEmoji = Emoji(
		shortcode = "foo",
		url = "https://social.elizabeth.cat/media/emoji/a8ba7r5sys3brmgk",
		fullEmojiId = "foo@tech.lgbt",
		instance = "tech.lgbt"
	)
)

val followNotification = Notification(
	id = "foo",
	forAccount = "foo-bar@tech.lgbt",
	createdAt = Instant.now(),
	type = NotificationType.FOLLOW,
	profile = defaultProfile,
	post = null,
	reaction = null,
	reactionEmoji = null
)
