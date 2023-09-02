package sh.elizabeth.wastodon.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import sh.elizabeth.wastodon.data.database.entity.PostEntity

@Dao
interface PostDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplace(vararg posts: PostEntity): List<Long>
}
