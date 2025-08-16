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
		repostedBy = null,
		poll = defaultPoll,
		emojis = emptyMap(),
		reactions = emptyMap(),
		myReaction = null,
		favorites = 3L,
		favorited = false,
	),
	repostedBy = defaultProfile,
	poll = defaultPoll,
	emojis = emptyMap(),
	reactions = emptyMap(),
	myReaction = null,
	favorites = 3L,
	favorited = true,
)
