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
	@Transaction
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplace(vararg posts: PostEntity): List<Long>

	@Transaction
	@Query(
		"""
		SELECT PostEntity.*, author.profileRow AS author_profileRow, author.profileId AS author_profileId, author.avatarUrl AS author_avatarUrl, author.fullUsername AS author_fullUsername, author.headerUrl AS author_headerUrl, author.instance AS author_instance, author.name AS author_name, author.username AS author_username FROM PostEntity
		JOIN ProfileEntity author ON author.profileId = PostEntity.authorId
		WHERE PostEntity.postId = :postId"""
	)
	suspend fun getPost(postId: String): PostWithAuthor?
}
