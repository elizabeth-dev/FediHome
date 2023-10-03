package sh.elizabeth.fedihome.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import sh.elizabeth.fedihome.model.Emoji

@Entity(indices = [Index(value = ["fullEmojiId"], unique = true)])
data class EmojiEntity(
	@PrimaryKey val fullEmojiId: String,
	val instance: String,
	val shortcode: String,
	val url: String,
)

fun EmojiEntity.toDomain(): Emoji = Emoji(
	fullEmojiId = fullEmojiId,
	instance = instance,
	shortcode = shortcode,
	url = url,
)
