package sh.elizabeth.fedihome.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import sh.elizabeth.fedihome.data.database.entity.EnrichedNotification
import sh.elizabeth.fedihome.data.database.entity.NotificationEntity

@Dao
interface NotificationDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplace(vararg notifications: NotificationEntity): List<Long>

	@Transaction
	@Query(
		"""
		SELECT NotificationEntity.*,

		profile.profileId AS profile_profileId, profile.name AS profile_name, profile.username AS profile_username, profile.instance AS profile_instance, profile.fullUsername AS profile_fullUsername, profile.avatarUrl AS profile_avatarUrl, profile.avatarBlur AS profile_avatarBlur,
		
		profileExtra.headerUrl AS profile_headerUrl, profileExtra.headerBlur AS profile_headerBlur, profileExtra.description AS profile_description, profileExtra.following AS profile_following, profileExtra.followers AS profile_followers, profileExtra.postCount AS profile_postCount, profileExtra.createdAt AS profile_createdAt, profileExtra.fields AS profile_fields, 
		
		post.postId AS post_postId, post.createdAt AS post_createdAt, post.updatedAt AS post_updatedAt, post.cw AS post_cw, post.text AS post_text, post.poll AS post_poll,
		
		author.profileId AS post_author_profileId, author.avatarUrl AS post_author_avatarUrl, author.avatarBlur AS post_author_avatarBlur, author.fullUsername AS post_author_fullUsername, authorExtra.profileRef AS post_author_profileRef, authorExtra.headerUrl AS post_author_headerUrl, author.instance AS post_author_instance, author.name AS post_author_name, author.username AS post_author_username, authorExtra.headerBlur AS post_author_headerBlur, authorExtra.description AS post_author_description, authorExtra.following AS post_author_following, authorExtra.followers AS post_author_followers, authorExtra.postCount AS post_author_postCount, authorExtra.createdAt AS post_author_createdAt, authorExtra.fields AS post_author_fields,
		
		quote.postId AS post_quotePost_postId, quote.authorId AS post_quotePost_authorId, quote.createdAt AS post_quotePost_createdAt, quote.updatedAt AS post_quotePost_updatedAt, quote.cw AS post_quotePost_cw, quote.text AS post_quotePost_text, quote.poll AS post_quotePost_poll, null AS post_quotePost_quoteId,
		
		quoteAuthor.profileId AS post_quoteAuthor_profileId, quoteAuthor.avatarUrl AS post_quoteAuthor_avatarUrl, quoteAuthor.avatarBlur AS post_quoteAuthor_avatarBlur, quoteAuthor.fullUsername AS post_quoteAuthor_fullUsername, quoteAuthor.instance AS post_quoteAuthor_instance, quoteAuthor.name AS post_quoteAuthor_name, quoteAuthor.username AS post_quoteAuthor_username, quoteAuthorExtra.profileRef AS post_quoteAuthor_profileRef, quoteAuthorExtra.headerUrl AS post_quoteAuthor_headerUrl, quoteAuthorExtra.headerBlur AS post_quoteAuthor_headerBlur, quoteAuthorExtra.description AS post_quoteAuthor_description, quoteAuthorExtra.following AS post_quoteAuthor_following, quoteAuthorExtra.followers AS post_quoteAuthor_followers, quoteAuthorExtra.postCount AS post_quoteAuthor_postCount, quoteAuthorExtra.createdAt AS post_quoteAuthor_createdAt, quoteAuthorExtra.fields AS post_quoteAuthor_fields

		FROM NotificationEntity 
		LEFT JOIN ProfileEntity profile ON NotificationEntity.profileId = profile.profileId
		LEFT JOIN ProfileExtraEntity profileExtra ON NotificationEntity.profileId = profileExtra.profileRef
		LEFT JOIN PostEntity post ON post.postId = NotificationEntity.postId
		LEFT JOIN ProfileEntity author ON author.profileId = post.authorId
		LEFT JOIN ProfileExtraEntity authorExtra ON authorExtra.profileRef = post.authorId
		LEFT JOIN PostEntity quote ON quote.postId = post.quoteId
		LEFT JOIN ProfileEntity quoteAuthor ON quoteAuthor.profileId = quote.authorId
		LEFT JOIN ProfileExtraEntity quoteAuthorExtra ON quoteAuthorExtra.profileRef = quote.authorId
		
		WHERE NotificationEntity.forAccount = :profileIdentifier
		ORDER BY NotificationEntity.notificationRow DESC"""
	)
	fun getNotificationsFlow(profileIdentifier: String): Flow<List<EnrichedNotification>>
}
