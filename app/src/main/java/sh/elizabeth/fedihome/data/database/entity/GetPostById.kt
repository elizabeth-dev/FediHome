package sh.elizabeth.fedihome.data.database.entity

import sh.elizabeth.fedihome.GetPostById
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.model.ProfileField

fun GetPostById.toPostDomain() = Post(
	id = postId,
	createdAt = createdAt,
	updatedAt = updatedAt,
	cw = cw,
	text = text,
//	author = author.toDomain(authorEmojis),
	author = Profile(
		id = profileId,
		username = username,
		instance = instance,
		name = name,
		description = description,
		following = following,
		followers = followers,
		postCount = postCount,
		createdAt = createdAt_,
		fields = fields.map {
			ProfileField(name = it.name, value = it.value)
		},
		avatarUrl = avatarUrl,
		avatarBlur = avatarBlur,
		headerUrl = headerUrl,
		headerBlur = headerBlur,
		emojis = emptyMap(),
	),
	repostedBy = null,
	quote = if (postId_ != null && profileId_ != null) Post(
		id = postId_,
		createdAt = createdAt__,
		updatedAt = updatedAt_,
		cw = cw_,
		text = text_,
		author = Profile(
			id = profileId_,
			username = username_!!,
			instance = instance_!!,
			name = name_,
			description = description_,
			following = following_,
			followers = followers_,
			postCount = postCount_,
			createdAt = createdAt_,
			fields = fields_!!.map {
				ProfileField(name = it.name, value = it.value)
			},
			avatarUrl = avatarUrl_,
			avatarBlur = avatarBlur_,
			headerUrl = headerUrl_,
			headerBlur = headerBlur_,
			emojis = emptyMap(),
		),
		repostedBy = null,
		quote = null,
		poll = poll_?.toDomain(),
		emojis = emptyMap(),
	) else null,
	poll = poll?.toDomain(),
//	emojis = postEmojis.associate { Pair(it.shortcode, it.toDomain()) })
	emojis = emptyMap(),
)