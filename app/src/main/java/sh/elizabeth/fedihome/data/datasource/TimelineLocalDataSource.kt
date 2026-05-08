package sh.elizabeth.fedihome.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.database.entity.toPostDomain
import sh.elizabeth.fedihome.model.Post
import javax.inject.Inject

class TimelineLocalDataSource @Inject constructor(private val appDatabase: AppDatabase) {
	fun insert(profileIdentifier: String, vararg posts: String) {
		posts.forEach { post ->
			appDatabase.timelinePostQueries.insert(
				profileIdentifier, post
			)
		}
	}

	fun existsInTimeline(profileIdentifier: String, postId: String): Boolean =
		appDatabase.timelinePostQueries.existsInTimeline(profileIdentifier, postId)
			.executeAsOne() > 0

	fun deleteByProfileId(profileIdentifier: String) {
		appDatabase.timelinePostQueries.deleteByProfileId(profileIdentifier)
	}

	fun getTimelinePosts(profileIdentifier: String, limit: Long, offset: Long): Flow<List<Post>> =
		appDatabase.timelinePostQueries
			.getTimelinePosts(profileIdentifier, limit, offset)
			.asFlow()
			.mapToList(Dispatchers.IO)
			.map { posts ->
				posts.map { it.toPostDomain() }
			}
}
