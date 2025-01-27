package sh.elizabeth.fedihome.data.datasource

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import sh.elizabeth.fedihome.api.firefish.PostFirefishApi
import sh.elizabeth.fedihome.api.firefish.model.toDomain
import sh.elizabeth.fedihome.api.mastodon.PostMastodonApi
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.api.sharkey.PostSharkeyApi
import sh.elizabeth.fedihome.api.sharkey.model.toDomain
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.PostDraft
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(
	private val postFirefishApi: PostFirefishApi,
	private val postSharkeyApi: PostSharkeyApi,
	private val postMastodonApi: PostMastodonApi,
) {
	suspend fun createPost(
		instance: String,
		endpoint: String,
		instanceType: SupportedInstances,
		token: String,
		newPost: PostDraft,
	): Post = when (instanceType) {
		SupportedInstances.FIREFISH -> postFirefishApi.createPost(
			endpoint = endpoint, token = token, newPost = newPost
		).createdNote.toDomain(instance)

		SupportedInstances.SHARKEY -> postSharkeyApi.createPost(
			endpoint = endpoint, token = token, newPost = newPost
		).createdNote.toDomain(instance)

		SupportedInstances.GLITCH,
		SupportedInstances.MASTODON,
			-> postMastodonApi.createPost(endpoint = endpoint, token = token, newPost = newPost)
			.toDomain(instance)

	}

	suspend fun fetchPost(
		instance: String,
		endpoint: String,
		instanceType: SupportedInstances,
		token: String,
		postId: String,
	): Post = when (instanceType) {
		SupportedInstances.FIREFISH -> postFirefishApi.fetchPost(
			endpoint = endpoint, token = token, postId = postId
		).toDomain(instance)

		SupportedInstances.SHARKEY -> postSharkeyApi.fetchPost(
			endpoint = endpoint, token = token, postId = postId
		).toDomain(instance)

		SupportedInstances.GLITCH,
		SupportedInstances.MASTODON,
			-> postMastodonApi.fetchPost(endpoint = endpoint, token = token, postId = postId)
			.toDomain(instance)
	}

	suspend fun fetchPostsByProfile(
		instance: String,
		endpoint: String,
		instanceType: SupportedInstances,
		token: String,
		profileId: String,
	): List<Post> = when (instanceType) {
		SupportedInstances.FIREFISH -> postFirefishApi.fetchPostsByProfile(
			endpoint = endpoint, token = token, profileId = profileId
		).map { it.toDomain(instance) }

		SupportedInstances.SHARKEY -> postSharkeyApi.fetchPostsByProfile(
			endpoint = endpoint, token = token, profileId = profileId
		).map { it.toDomain(instance) }

		SupportedInstances.GLITCH,
		SupportedInstances.MASTODON,
			-> postMastodonApi.fetchPostsByProfile(
			endpoint = endpoint, token = token, profileId = profileId
		).map { it.toDomain(instance) }
	}

	suspend fun votePoll(
		endpoint: String,
		instanceType: SupportedInstances,
		token: String,
		pollId: String,
		choices: List<Int>,
	) = when (instanceType) {
		SupportedInstances.FIREFISH, SupportedInstances.SHARKEY -> coroutineScope {
			val voteCoroutines = choices.map {
				async {
					postFirefishApi.votePoll(
						endpoint = endpoint, token = token, postId = pollId, choice = it
					)
				}
			}

			voteCoroutines.awaitAll()
			return@coroutineScope
		}

		SupportedInstances.GLITCH,
		SupportedInstances.MASTODON,
			-> postMastodonApi.votePoll(
			endpoint = endpoint, token = token, pollId = pollId, choices = choices
		)
	}
}
