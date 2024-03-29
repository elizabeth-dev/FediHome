package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.EmojiEntity
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.model.Emoji
import javax.inject.Inject

class EmojiLocalDataSource @Inject constructor(private val appDatabase: AppDatabase) {
	fun insertOrReplace(vararg emojis: Emoji) =
		appDatabase.emojiQueries.transaction {
			emojis.forEach { emoji ->
				appDatabase.emojiQueries.insertOrReplace(
					EmojiEntity(
						emojiId = emoji.fullEmojiId,
						instance = emoji.instance,
						shortcode = emoji.shortcode,
						url = emoji.url,
					)
				)
			}
		}
}
