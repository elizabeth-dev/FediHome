package sh.elizabeth.fedihome.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import sh.elizabeth.fedihome.data.database.entity.EnrichedTimelinePost
import sh.elizabeth.fedihome.data.database.entity.TimelinePostEntity

@Dao
interface TimelineDao {
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertTimelinePost(vararg timelinePost: TimelinePostEntity): List<Long>

	@Transaction
	@Query(
		"""
		SELECT PostEntity.*,
		
		author.profileId AS author_profileId, author.avatarUrl AS author_avatarUrl, author.avatarBlur AS author_avatarBlur, author.fullUsername AS author_fullUsername, authorExtra.profileRef AS author_profileRef, authorExtra.headerUrl AS author_headerUrl, author.instance AS author_instance, author.name AS author_name, author.username AS author_username, authorExtra.headerBlur AS author_headerBlur, authorExtra.description AS author_description, authorExtra.following AS author_following, authorExtra.followers AS author_followers, authorExtra.postCount AS author_postCount, authorExtra.createdAt AS author_createdAt, authorExtra.fields AS author_fields,
		
		repostedBy.profileId AS repostedBy_profileId, repostedBy.avatarUrl AS repostedBy_avatarUrl, repostedBy.avatarBlur AS repostedBy_avatarBlur, repostedBy.fullUsername AS repostedBy_fullUsername, repostedBy.instance AS repostedBy_instance, repostedBy.name AS repostedBy_name, repostedBy.username AS repostedBy_username, repostedByExtra.profileRef AS repostedBy_profileRef, repostedByExtra.headerUrl AS repostedBy_headerUrl, repostedByExtra.headerBlur AS repostedBy_headerBlur, repostedByExtra.description AS repostedBy_description, repostedByExtra.following AS repostedBy_following, repostedByExtra.followers AS repostedBy_followers, repostedByExtra.postCount AS repostedBy_postCount, repostedByExtra.createdAt AS repostedBy_createdAt, repostedByExtra.fields AS repostedBy_fields,
		
		quote.postId AS quotePost_postId, quote.authorId AS quotePost_authorId, quote.createdAt AS quotePost_createdAt, quote.updatedAt AS quotePost_updatedAt, quote.cw AS quotePost_cw, quote.text AS quotePost_text, quote.poll AS quotePost_poll, null AS quotePost_quoteId,
		
		quoteAuthor.profileId AS quoteAuthor_profileId, quoteAuthor.avatarUrl AS quoteAuthor_avatarUrl, quoteAuthor.avatarBlur AS quoteAuthor_avatarBlur, quoteAuthor.fullUsername AS quoteAuthor_fullUsername, quoteAuthor.instance AS quoteAuthor_instance, quoteAuthor.name AS quoteAuthor_name, quoteAuthor.username AS quoteAuthor_username, quoteAuthorExtra.profileRef AS quoteAuthor_profileRef, quoteAuthorExtra.headerUrl AS quoteAuthor_headerUrl, quoteAuthorExtra.headerBlur AS quoteAuthor_headerBlur, quoteAuthorExtra.description AS quoteAuthor_description, quoteAuthorExtra.following AS quoteAuthor_following, quoteAuthorExtra.followers AS quoteAuthor_followers, quoteAuthorExtra.postCount AS quoteAuthor_postCount, quoteAuthorExtra.createdAt AS quoteAuthor_createdAt, quoteAuthorExtra.fields AS quoteAuthor_fields

		FROM TimelinePostEntity 
		JOIN PostEntity ON PostEntity.postId = TimelinePostEntity.timelinePostId
		JOIN ProfileEntity author ON author.profileId = PostEntity.authorId
		LEFT JOIN ProfileExtraEntity authorExtra ON authorExtra.profileRef = PostEntity.authorId
		LEFT JOIN ProfileEntity repostedBy ON repostedBy.profileId = TimelinePostEntity.repostedBy
		LEFT JOIN ProfileExtraEntity repostedByExtra ON repostedByExtra.profileRef = TimelinePostEntity.repostedBy
		LEFT JOIN PostEntity quote ON quote.postId = PostEntity.quoteId
		LEFT JOIN ProfileEntity quoteAuthor ON quoteAuthor.profileId = quote.authorId
		LEFT JOIN ProfileExtraEntity quoteAuthorExtra ON quoteAuthorExtra.profileRef = quote.authorId
		
		WHERE TimelinePostEntity.profileIdentifier = :profileIdentifier
		ORDER BY TimelinePostEntity.timelinePostRow DESC"""
	)
	fun getTimelinePosts(profileIdentifier: String): Flow<List<EnrichedTimelinePost>>
}
