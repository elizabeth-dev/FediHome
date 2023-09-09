package sh.elizabeth.wastodon.data.repository

import sh.elizabeth.wastodon.data.datasource.PostLocalDataSource
import sh.elizabeth.wastodon.data.datasource.PostRemoteDataSource
import sh.elizabeth.wastodon.data.model.toDomain
import sh.elizabeth.wastodon.model.Post
import sh.elizabeth.wastodon.model.PostDraft
import sh.elizabeth.wastodon.model.unwrapProfiles
import sh.elizabeth.wastodon.model.unwrapQuotes
import javax.inject.Inject

class PostRepository @Inject constructor(
	private val postLocalDataSource: PostLocalDataSource,
	private val postRemoteDataSource: PostRemoteDataSource,
	private val profileRepository: ProfileRepository,
) {
	suspend fun insertOrReplace(vararg posts: Post) {
		postLocalDataSource.insertOrReplace(*posts)
	}

	suspend fun createPost(instance: String, newPost: PostDraft) {
		val postRes = postRemoteDataSource.createPost(instance, newPost)
		insertOrReplace(postRes.createdNote.toDomain(instance))
	}

	suspend fun getPost(postId: String): Post? = postLocalDataSource.getPost(postId)

	fun getPostFlow(postId: String) = postLocalDataSource.getPostFlow(postId)

	fun getPostsByProfileFlow(profileId: String) =
		postLocalDataSource.getPostsByProfileFlow(profileId)

	suspend fun fetchPost(instance: String, postId: String) {
		val postRes = postRemoteDataSource.fetchPost(instance, postId)
		postLocalDataSource.insertOrReplace(postRes.toDomain(instance))
	}

	suspend fun fetchPostsByProfile(instance: String, profileId: String) {
		val posts = postRemoteDataSource.fetchPostsByProfile(instance, profileId)
			.map { it.toDomain(instance) }
			.flatMap { it.unwrapQuotes() }
		val profiles = posts.flatMap { it.unwrapProfiles() }.toSet()

		profileRepository.insertOrReplace(*profiles.toTypedArray())
		insertOrReplace(*posts.toTypedArray())
	}

	suspend fun votePoll(instance: String, postId: String, choice: Int) =
		postRemoteDataSource.votePoll(instance, postId, choice)

}
