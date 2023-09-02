package sh.elizabeth.wastodon.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import sh.elizabeth.wastodon.data.database.entity.PostWithAuthor
import sh.elizabeth.wastodon.data.database.entity.TimelinePostCrossRefEntity

@Dao
interface TimelineDao {
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertTimelinePost(vararg timelinePost: TimelinePostCrossRefEntity): List<Long>

	@Transaction
	@Query("SELECT PostEntity.* FROM TimelinePostCrossRefEntity JOIN PostEntity ON PostEntity.postId = TimelinePostCrossRefEntity.timelinePostId WHERE TimelinePostCrossRefEntity.profileIdentifier = :profileIdentifier ORDER BY TimelinePostCrossRefEntity.timelinePostRow DESC")
	fun getTimelinePosts(profileIdentifier: String): Flow<List<PostWithAuthor>>
}
