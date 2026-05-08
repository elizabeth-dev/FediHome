package sh.elizabeth.fedihome.data.database.entity

import sh.elizabeth.fedihome.GetEmojisForPosts
import sh.elizabeth.fedihome.GetEmojisForProfiles
import sh.elizabeth.fedihome.GetTimelinePosts
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.model.ProfileField

fun GetTimelinePosts.toPostDomain(
	postEmojiList: List<GetEmojisForPosts>, profileEmojiList: List<GetEmojisForProfiles>,
): Post {
	val postEmojis = postEmojiList.associate { it.emojiId to it.toDomain() }
	val profileEmojis = profileEmojiList.associate { it.emojiId to it.toDomain() }
	return Post(
		id = postId_,
		createdAt = createdAt,
		updatedAt = updatedAt,
		cw = cw,
		text = text,
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
			emojis = profileEmojis,
		),
		boosted = boosted,
		boosts = boostsCount,
		boostedPost = if (postId___ != null && profileId__ != null) Post(
			id = postId___,
			createdAt = createdAt____,
			updatedAt = updatedAt__,
			cw = cw__,
			text = text__,
			author = Profile(
				id = profileId__,
				username = username__!!,
				instance = instance__!!,
				name = name__,
				description = description__,
				following = following__,
				followers = followers__,
				postCount = postCount__,
				createdAt = createdAt__,
				fields = fields__!!.map {
					ProfileField(name = it.name, value = it.value)
				},
				avatarUrl = avatarUrl__,
				avatarBlur = avatarBlur__,
				headerUrl = headerUrl__,
				headerBlur = headerBlur__,
				emojis = profileEmojis,
			),
			boosted = boosted__!!,
			boosts = boostsCount__!!,
			quote = null,
			poll = poll__?.toDomain(),
			emojis = postEmojis,
			reactions = reactions__ ?: emptyMap(),
			myReactions = myReactions__ ?: emptyList(),
			favorites = favoriteCount__!!,
			favorited = favorited__!!,
			mentionLinksMap = mentionLinks__,
			attachments = attachments__?.map(AttachmentEntity::toDomain) ?: emptyList(),
			inReplyToId = inReplyToId__
		)
		else null,
		quote = if (postId__ != null && profileId_ != null) Post(
			id = postId__,
			createdAt = createdAt___,
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
				emojis = profileEmojis,
			),
			boosted = boosted_!!,
			boosts = boostsCount_!!,
			quote = null,
			poll = poll_?.toDomain(),
			emojis = postEmojis,
			reactions = reactions_ ?: emptyMap(),
			myReactions = myReactions_ ?: emptyList(),
			favorites = favoriteCount_!!,
			favorited = favorited_!!,
			mentionLinksMap = mentionLinks_,
			attachments = attachments_?.map(AttachmentEntity::toDomain) ?: emptyList(),
			inReplyToId = inReplyToId_
		)
		else null,
		poll = poll?.toDomain(),
		emojis = postEmojis,
		reactions = reactions ?: emptyMap(),
		myReactions = myReactions ?: emptyList(),
		favorites = favoriteCount,
		favorited = favorited,
		mentionLinksMap = mentionLinks,
		attachments = attachments?.map(AttachmentEntity::toDomain) ?: emptyList(),
		inReplyToId = inReplyToId
	)
}