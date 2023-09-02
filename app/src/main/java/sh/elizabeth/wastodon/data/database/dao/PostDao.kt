package sh.elizabeth.wastodon.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import sh.elizabeth.wastodon.data.database.entity.PostEntity
import sh.elizabeth.wastodon.data.database.entity.PostWithAuthor

@Dao
interface PostDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplace(vararg posts: PostEntity): List<Long>

	@Transaction
	@Query("SELECT * FROM PostEntity WHERE postId = :postId")
	suspend fun getPost(postId: String): PostWithAuthor?
}
