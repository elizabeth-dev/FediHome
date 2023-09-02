package sh.elizabeth.wastodon.data.repository

import kotlinx.coroutines.flow.Flow
import sh.elizabeth.wastodon.data.datasource.TimelineLocalDataSource
import sh.elizabeth.wastodon.data.datasource.TimelineRemoteDataSource
import sh.elizabeth.wastodon.data.model.toDomain
import sh.elizabeth.wastodon.model.Post
import javax.inject.Inject

class TimelineRepository @Inject constructor(
	private val timelineLocalDataSource: TimelineLocalDataSource,
	private val timelineRemoteDataSource: TimelineRemoteDataSource,
	private val postRepository: PostRepository,
	private val profileRepository: ProfileRepository,
) {
	fun getTimeline(profileIdentifier: String): Flow<List<Post>> =
		timelineLocalDataSource.getTimelinePosts(profileIdentifier)

	suspend fun fetchTimeline(instance: String, profileIdentifier: String) {
		val posts = timelineRemoteDataSource.getHome(instance).map { it.toDomain(instance) }
		val profiles = posts.map(Post::author).toSet()

		profileRepository.insertOrReplace(*profiles.toTypedArray())
		postRepository.insertOrReplace(*posts.toTypedArray())
		timelineLocalDataSource.insert(profileIdentifier, *posts.map(Post::id).toTypedArray())
	}
}
