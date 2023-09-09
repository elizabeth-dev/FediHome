package sh.elizabeth.wastodon.data.repository

import kotlinx.coroutines.flow.Flow
import sh.elizabeth.wastodon.data.datasource.TimelineLocalDataSource
import sh.elizabeth.wastodon.data.datasource.TimelinePost
import sh.elizabeth.wastodon.data.datasource.TimelineRemoteDataSource
import sh.elizabeth.wastodon.data.model.toDomain
import sh.elizabeth.wastodon.model.Post
import sh.elizabeth.wastodon.model.unwrapProfiles
import sh.elizabeth.wastodon.model.unwrapQuotes
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
		val unWrappedPosts = posts.flatMap { it.unwrapQuotes() }
		val profiles = unWrappedPosts.flatMap { it.unwrapProfiles() }.toSet()

		profileRepository.insertOrReplaceMain(*profiles.toTypedArray())
		postRepository.insertOrReplace(*unWrappedPosts.toTypedArray())
		timelineLocalDataSource.insert(
			profileIdentifier,
			*posts.map { TimelinePost(postId = it.id, repostedById = it.repostedBy?.id) }
				.reversed()
				.toTypedArray()
		)
	}
}
