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

		author.profileRow AS author_profileRow, author.profileId AS author_profileId, author.avatarUrl AS author_avatarUrl, author.avatarBlur AS author_avatarBlur, author.fullUsername AS author_fullUsername, author.headerUrl AS author_headerUrl, author.headerBlur AS author_headerBlur, author.description AS author_description, author.following AS author_following, author.followers AS author_followers, author.postCount AS author_postCount, author.createdAt AS author_createdAt, author.fields AS author_fields, author.instance AS author_instance, author.name AS author_name, author.username AS author_username,

		quote.postRow AS quotePost_postRow, quote.postId AS quotePost_postId, quote.authorId AS quotePost_authorId, quote.createdAt AS quotePost_createdAt, quote.updatedAt AS quotePost_updatedAt, quote.cw AS quotePost_cw, quote.text AS quotePost_text, quote.poll AS quotePost_poll, null AS quotePost_quoteId,
		
		quoteAuthor.profileRow AS quoteAuthor_profileRow, quoteAuthor.profileId AS quoteAuthor_profileId, quoteAuthor.avatarUrl AS quoteAuthor_avatarUrl, quoteAuthor.avatarBlur AS quoteAuthor_avatarBlur, quoteAuthor.fullUsername AS quoteAuthor_fullUsername, quoteAuthor.headerUrl AS quoteAuthor_headerUrl, quoteAuthor.headerBlur AS quoteAuthor_headerBlur, quoteAuthor.description AS quoteAuthor_description, quoteAuthor.following AS quoteAuthor_following, quoteAuthor.followers AS quoteAuthor_followers, quoteAuthor.postCount AS quoteAuthor_postCount, quoteAuthor.createdAt AS quoteAuthor_createdAt, quoteAuthor.fields AS quoteAuthor_fields, quoteAuthor.instance AS quoteAuthor_instance, quoteAuthor.name AS quoteAuthor_name, quoteAuthor.username AS quoteAuthor_username

		FROM PostEntity
		JOIN ProfileEntity author ON author.profileId = PostEntity.authorId
		LEFT JOIN PostEntity quote ON quote.postId = PostEntity.quoteId
		LEFT JOIN ProfileEntity quoteAuthor ON quoteAuthor.profileId = quote.authorId"""

@Dao
interface PostDao {
	@Transaction
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplace(vararg posts: PostEntity): List<Long>

	@Transaction
	@Query("$GET_POST_QUERY WHERE PostEntity.postId = :postId")
	suspend fun getPost(postId: String): PostWithAuthor?

	@Transaction
	@Query("$GET_POST_QUERY WHERE PostEntity.postId = :postId")
	fun getPostFlow(postId: String): Flow<PostWithAuthor>

	@Transaction
	@Query("$GET_POST_QUERY WHERE PostEntity.authorId = :profileId ORDER BY PostEntity.createdAt DESC")
	fun getPostsByProfileFlow(profileId: String): Flow<List<PostWithAuthor>>
}
