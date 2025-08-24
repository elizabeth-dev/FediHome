package sh.elizabeth.fedihome.data.database.entity

import sh.elizabeth.fedihome.EmojiEntity
import sh.elizabeth.fedihome.GetEmojisForPosts
import sh.elizabeth.fedihome.GetEmojisForProfiles
import sh.elizabeth.fedihome.model.Emoji

fun EmojiEntity.toDomain() = Emoji(
	fullEmojiId = emojiId,
	instance = instance,
	shortcode = shortcode,
	url = url,
)

fun GetEmojisForPosts.toDomain() = Emoji(
	fullEmojiId = emojiId,
	instance = instance,
	shortcode = shortcode,
	url = url,
)

fun GetEmojisForProfiles.toDomain() = Emoji(
	fullEmojiId = emojiId,
	instance = instance,
	shortcode = shortcode,
	url = url,
)