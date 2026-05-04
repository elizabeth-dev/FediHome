package sh.elizabeth.fedihome.data.database.entity

import sh.elizabeth.fedihome.GetEmojisForPosts
import sh.elizabeth.fedihome.GetEmojisForProfiles
import sh.elizabeth.fedihome.GetNotificationByAccount
import sh.elizabeth.fedihome.model.Emoji
import sh.elizabeth.fedihome.model.Notification
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.model.ProfileField

fun GetNotificationByAccount.toDomain(
	postEmojiList: List<GetEmojisForPosts>, profileEmojiList: List<GetEmojisForProfiles>
): Notification {
	val postEmojis = postEmojiList.associate { it.emojiId to it.toDomain() }
	val profileEmojis = profileEmojiList.associate { it.emojiId to it.toDomain() }

	return Notification(
		id = notificationId,
		forAccount = forAccount,
		createdAt = createdAt,
		type = type,
		reaction = reaction,
		reactionEmoji = if (emojiId != null && instance__ != null && shortcode != null && url != null) Emoji(
			fullEmojiId = emojiId,
			instance = instance__,
			shortcode = shortcode,
			url = url,
		) else null,
		profile = if (profileId_ != null) Profile(
			id = profileId_,
			name = name!!,
			username = username!!,
			instance = instance!!,
			avatarUrl = avatarUrl,
			avatarBlur = avatarBlur,
			headerUrl = headerUrl,
			headerBlur = headerBlur,
			description = description,
			following = following,
			followers = followers,
			postCount = postCount,
			createdAt = createdAt_,
			fields = fields!!.map {
				ProfileField(name = it.name, value = it.value)
			},
			emojis = profileEmojis
		) else null,
		post = if (postId_ != null && profileId__ !== null) Post(
			id = postId_,
			createdAt = createdAt__,
			updatedAt = updatedAt,
			cw = cw,
			text = text,
			// author = author.toDomain(authorEmojis),
			author = Profile(
				id = profileId__,
				username = username_!!,
				instance = instance_!!,
				name = name_,
				description = description_,
				following = following_,
				followers = followers_,
				postCount = postCount_,
				createdAt = createdAt___,
				fields = fields_!!.map {
					ProfileField(name = it.name, value = it.value)
				},
				avatarUrl = avatarUrl_,
				avatarBlur = avatarBlur_,
				headerUrl = headerUrl_,
				headerBlur = headerBlur_,
				emojis = profileEmojis,
			),
			emojis = postEmojis,
			boostedBy = null,
			boosted = boosted!!,
			boosts = boostsCount!!,
			quote = if (postId__ != null && profileId___ != null) Post(
				id = postId__,
				createdAt = createdAt___,
				updatedAt = updatedAt_,
				cw = cw_,
				text = text_,
				// author = author.toDomain(authorEmojis),
				author = Profile(
					id = profileId___,
					username = username__!!,
					instance = instance___!!,
					name = name__,
					description = description__,
					following = following__,
					followers = followers__,
					postCount = postCount__,
					createdAt = createdAt____,
					fields = fields__!!.map {
						ProfileField(name = it.name, value = it.value)
					},
					avatarUrl = avatarUrl__,
					avatarBlur = avatarBlur__,
					headerUrl = headerUrl__,
					headerBlur = headerBlur__,
					emojis = profileEmojis,
				),
				emojis = postEmojis,
				boostedBy = null,
				boosted = boosted_!!,
				boosts = boostsCount_!!,
				quote = null,
				poll = poll_?.toDomain(),
				reactions = reactions_ ?: emptyMap(),
				myReactions = myReactions_ ?: emptyList(),
				favorites = favoriteCount_!!,
				favorited = favorited_!!,
				attachments = attachments_?.map(AttachmentEntity::toDomain) ?: emptyList(),
			) else null,
			poll = poll?.toDomain(),
			reactions = reactions ?: emptyMap(),
			myReactions = myReactions ?: emptyList(),
			favorites = favoriteCount!!,
			favorited = favorited!!,
			attachments = attachments?.map(AttachmentEntity::toDomain) ?: emptyList(),
		) else null
	)
}