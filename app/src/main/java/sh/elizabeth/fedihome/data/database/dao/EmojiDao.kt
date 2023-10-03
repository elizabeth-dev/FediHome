package sh.elizabeth.fedihome.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import sh.elizabeth.fedihome.data.database.entity.EmojiEntity

@Dao
interface EmojiDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplace(vararg emojis: EmojiEntity): List<Long>
}
