package sh.elizabeth.wastodon.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import sh.elizabeth.wastodon.data.database.entity.PostEntity
import sh.elizabeth.wastodon.data.database.entity.PostWithAuthor

const val GET_POST_QUERY = """
		SELECT PostEntity.*,

		author.profileRow AS author_profileRow, author.profileId AS author_profileId, author.avatarUrl AS author_avatarUrl, author.avatarBlur AS author_avatarBlur, author.fullUsername AS author_fullUsername, author.headerUrl AS author_headerUrl, author.instance AS author_instance, author.name AS author_name, author.username AS author_username,

		quote.postRow AS quotePost_postRow, quote.postId AS quotePost_postId, quote.authorId AS quotePost_authorId, quote.createdAt AS quotePost_createdAt, quote.updatedAt AS quotePost_updatedAt, quote.cw AS quotePost_cw, quote.text AS quotePost_text, quote.poll AS quotePost_poll, null AS quotePost_quoteId,
		
		quoteAuthor.profileRow AS quoteAuthor_profileRow, quoteAuthor.profileId AS quoteAuthor_profileId, quoteAuthor.avatarUrl AS quoteAuthor_avatarUrl, quoteAuthor.avatarBlur AS quoteAuthor_avatarBlur, quoteAuthor.fullUsername AS quoteAuthor_fullUsername, quoteAuthor.headerUrl AS quoteAuthor_headerUrl, quoteAuthor.instance AS quoteAuthor_instance, quoteAuthor.name AS quoteAuthor_name, quoteAuthor.username AS quoteAuthor_username

		FROM PostEntity
		JOIN ProfileEntity author ON author.profileId = PostEntity.authorId
		LEFT JOIN PostEntity quote ON quote.postId = PostEntity.quoteId
		LEFT JOIN ProfileEntity quoteAuthor ON quoteAuthor.profileId = quote.authorId
		WHERE PostEntity.postId = :postId"""

@Dao
interface PostDao {
	@Transaction
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplace(vararg posts: PostEntity): List<Long>

	@Transaction
	@Query(GET_POST_QUERY)
	suspend fun getPost(postId: String): PostWithAuthor?

	@Transaction
	@Query(GET_POST_QUERY)
	fun getPostFlow(postId: String): Flow<PostWithAuthor>
}
