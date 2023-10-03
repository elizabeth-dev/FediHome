package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.data.database.dao.EmojiDao
import sh.elizabeth.fedihome.data.database.entity.EmojiEntity
import sh.elizabeth.fedihome.model.Emoji
import javax.inject.Inject

class EmojiLocalDataSource @Inject constructor(private val emojiDao: EmojiDao) {
	suspend fun insertOrReplace(vararg emojis: Emoji): List<Long> =
		emojiDao.insertOrReplace(*emojis.map(Emoji::toEntity).toTypedArray())
}

fun Emoji.toEntity() = EmojiEntity(
	fullEmojiId = fullEmojiId,
	instance = instance,
	shortcode = shortcode,
	url = url,
)
