package sh.elizabeth.fedihome.mock

import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.model.ProfileField

val defaultProfile = Profile(
	id = "foo",
	username = "elizabeth",
	name = "Elizabeth",
	avatarUrl = null,
	avatarBlur = null,
	instance = "blahaj.zone",
	fullUsername = "elizabeth@blahaj.zone",
	headerUrl = null, headerBlur = null,
	following = 12,
	followers = 323,
	postCount = 34,
	createdAt = null,
	fields = listOf(
		ProfileField("Birthday", "April 20"),
		ProfileField("Location", "The Moon"),
		ProfileField("Website", "https://example.com"),
		ProfileField("Foo", "Bar"),
	),
	description = "Lorem Ipsum Dolor Sit Amet",
	emojis = emptyMap(),
)
