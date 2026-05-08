package sh.elizabeth.fedihome.mock

import sh.elizabeth.fedihome.model.Post
import java.time.Instant

val defaultPost = Post(
	id = "foo",
	createdAt = Instant.now(),
	updatedAt = null,
	cw = null,
	text = "bar",
	author = defaultProfile,
	quote = Post(
		id = "foo",
		createdAt = Instant.now(),
		updatedAt = null,
		cw = null,
		text = "bar",
		author = defaultProfile,
		quote = null,
		boosted = false,
		boosts = 3,
		poll = defaultPoll,
		emojis = emptyMap(),
		reactions = emptyMap(),
		myReactions = emptyList(),
		favorites = 3L,
		favorited = false,
		attachments = emptyList(),
		inReplyToId = null
	),
	boosted = true,
	boosts = 5,
	poll = defaultPoll,
	emojis = emptyMap(),
	reactions = mapOf(
		"👍" to 3,
		"👎" to 1,
		"😂" to 5,
		"🎉" to 2,
	),
	myReactions = listOf("👍"),
	favorites = 3L,
	favorited = false,
	attachments = emptyList(),
	inReplyToId = null
)
