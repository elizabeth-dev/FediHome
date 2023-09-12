package sh.elizabeth.wastodon.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sh.elizabeth.wastodon.data.database.dao.TimelineDao
import sh.elizabeth.wastodon.data.database.entity.EnrichedTimelinePost
import sh.elizabeth.wastodon.data.database.entity.TimelinePostEntity
import sh.elizabeth.wastodon.data.database.entity.toPostDomain
import sh.elizabeth.wastodon.model.Post
import javax.inject.Inject

class TimelineLocalDataSource @Inject constructor(private val timelineDao: TimelineDao) {
	suspend fun insert(profileIdentifier: String, vararg posts: TimelinePost): List<Long> =
		timelineDao.insertTimelinePost(*posts.map { post ->
			TimelinePostEntity(
				profileIdentifier = profileIdentifier,
				timelinePostId = post.postId,
				repostedBy = post.repostedById
			)
		}.toTypedArray())

	fun getTimelinePosts(profileIdentifier: String): Flow<List<Post>> =
		timelineDao.getTimelinePosts(profileIdentifier)
			.map { it.map(EnrichedTimelinePost::toPostDomain) }
}

data class TimelinePost(
	val postId: String,
	val repostedById: String?,
)
