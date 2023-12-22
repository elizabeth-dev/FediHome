package sh.elizabeth.fedihome.mock

import sh.elizabeth.fedihome.model.Notification
import sh.elizabeth.fedihome.model.NotificationType
import java.time.Instant

val mentionNotification = Notification(
	id= "foo",
	forAccount = "foo-bar@tech.lgbt",
	createdAt = Instant.now(),
	type = NotificationType.MENTION,
	profile = defaultProfile,
	post = defaultPost,
	reaction = null
)

val reactionNotification = Notification(
	id= "foo",
	forAccount = "foo-bar@tech.lgbt",
	createdAt = Instant.now(),
	type = NotificationType.REACTION,
	profile = defaultProfile,
	post = defaultPost,
	reaction = "⭐"
)

val followNotification = Notification(
	id= "foo",
	forAccount = "foo-bar@tech.lgbt",
	createdAt = Instant.now(),
	type = NotificationType.FOLLOW,
	profile = defaultProfile,
	post = null,
	reaction = null
)
