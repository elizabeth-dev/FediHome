package sh.elizabeth.fedihome.data.database.entity

import sh.elizabeth.fedihome.EmojiEntity
import sh.elizabeth.fedihome.model.Emoji

fun EmojiEntity.toDomain() = Emoji(
	fullEmojiId = emojiId,
	instance = instance,
	shortcode = shortcode,
	url = url,
)