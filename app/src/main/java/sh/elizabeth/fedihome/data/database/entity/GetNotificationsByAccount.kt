package sh.elizabeth.fedihome.data.database.entity

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sh.elizabeth.fedihome.GetNotificationsByAccount
import sh.elizabeth.fedihome.model.Emoji
import sh.elizabeth.fedihome.model.Notification
import sh.elizabeth.fedihome.model.NotificationPagingItem
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.model.ProfileField

private val gson = Gson()
private val emojiListType = object : TypeToken<List<PagingNotifEmojiJson>>() {}.type

private data class PagingNotifEmojiJson(
	val emojiId: String,
	val instance: String,
	val shortcode: String,
	val url: String,
)

private fun parsePagingNotifEmojisJson(json: String?): Map<String, Emoji> {
	if (json.isNullOrBlank() || json == "[null]") return emptyMap()
	val list: List<PagingNotifEmojiJson> = gson.fromJson(json, emojiListType)
	return list.associate {
		it.emojiId to Emoji(
			fullEmojiId = it.emojiId, instance = it.instance, shortcode = it.shortcode, url = it.url
		)
	}
}

fun GetNotificationsByAccount.toNotificationPagingItemDomain(): NotificationPagingItem {
	val postEmojis = parsePagingNotifEmojisJson(postEmojisJson)
	val notifProfileEmojis = parsePagingNotifEmojisJson(notifProfileEmojisJson)
	val postProfileEmojis = parsePagingNotifEmojisJson(postProfileEmojisJson)
	val quotePostEmojis = parsePagingNotifEmojisJson(quotePostEmojisJson)
	val quoteProfileEmojis = parsePagingNotifEmojisJson(quoteProfileEmojisJson)

	val notification = Notification(
		id = notificationId_,
		forAccount = forAccount_,
		createdAt = createdAt,
		type = type_,
		reaction = reaction,
		reactionEmoji = if (emojiId != null && instance__ != null && shortcode != null && url != null) Emoji(
			fullEmojiId = emojiId,
			instance = instance__,
			shortcode = shortcode,
			url = url,
		)
		else null,
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
			emojis = notifProfileEmojis
		)
		else null,
		post = if (postId_ != null && profileId__ !== null) Post(
			id = postId_,
			createdAt = createdAt__,
			updatedAt = updatedAt,
			cw = cw,
			text = text,
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
				emojis = postProfileEmojis,
			),
			emojis = postEmojis,
			boosted = boosted!!,
			boosts = boostsCount!!,
			quote = if (postId__ != null && profileId___ != null) Post(
				id = postId__,
				createdAt = createdAt___,
				updatedAt = updatedAt_,
				cw = cw_,
				text = text_,
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
					emojis = quoteProfileEmojis,
				),
				emojis = quotePostEmojis,
				boosted = boosted_!!,
				boosts = boostsCount_!!,
				quote = null,
				poll = poll_?.toDomain(),
				reactions = reactions_ ?: emptyMap(),
				myReactions = myReactions_ ?: emptyList(),
				favorites = favoriteCount_!!,
				favorited = favorited_!!,
				attachments = attachments_?.map(AttachmentEntity::toDomain) ?: emptyList(),
				inReplyToId = inReplyToId_
			)
			else null,
			poll = poll?.toDomain(),
			reactions = reactions ?: emptyMap(),
			myReactions = myReactions ?: emptyList(),
			favorites = favoriteCount!!,
			favorited = favorited!!,
			attachments = attachments?.map(AttachmentEntity::toDomain) ?: emptyList(),
			inReplyToId = inReplyToId
		)
		else null
	)

	return NotificationPagingItem(notification = notification, forAccount = forAccount, type = type)
}
