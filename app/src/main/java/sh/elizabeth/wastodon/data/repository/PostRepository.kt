package sh.elizabeth.wastodon.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import sh.elizabeth.wastodon.data.database.entity.PostEmojiCrossRef
import sh.elizabeth.wastodon.data.database.entity.ProfileEmojiCrossRef
import sh.elizabeth.wastodon.data.datasource.EmojiLocalDataSource
import sh.elizabeth.wastodon.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.wastodon.data.datasource.PostLocalDataSource
import sh.elizabeth.wastodon.data.datasource.PostRemoteDataSource
import sh.elizabeth.wastodon.model.Post
import sh.elizabeth.wastodon.model.PostDraft
import sh.elizabeth.wastodon.model.unwrapProfiles
import sh.elizabeth.wastodon.model.unwrapQuotes
import sh.elizabeth.wastodon.util.SupportedInstances
import javax.inject.Inject

class PostRepository @Inject constructor(
	private val postLocalDataSource: PostLocalDataSource,
	private val profileRepository: ProfileRepository,
	private val emojiLocalDataSource: EmojiLocalDataSource,
	private val postRemoteDataSource: PostRemoteDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {
	private suspend fun getInstanceAndTypeAndToken(activeAccount: String): Triple<String, SupportedInstances, String> =
		activeAccount.let {
			val internalData = internalDataLocalDataSource.internalData.first()
			val instance = it.split(':').first()
			Triple(instance, internalData.serverTypes[instance]!!, internalData.accessTokens[it]!!)
		}

	suspend fun insertOrReplace(vararg posts: Post) {
		postLocalDataSource.insertOrReplace(*posts)
	}

	suspend fun insertOrReplaceEmojiCrossRef(vararg refs: PostEmojiCrossRef) {
		postLocalDataSource.insertOrReplaceEmojiCrossRef(*refs)
	}

	suspend fun createPost(activeAccount: String, newPost: PostDraft) {
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(activeAccount)

		val postRes = postRemoteDataSource.createPost(instance, instanceType, token, newPost)
		insertOrReplace(postRes)
	}

	suspend fun getPost(postId: String): Post? = postLocalDataSource.getPost(postId)

	fun getPostFlow(postId: String) = postLocalDataSource.getPostFlow(postId)

	fun getPostsByProfileFlow(profileId: String) =
		postLocalDataSource.getPostsByProfileFlow(profileId)

	suspend fun fetchPost(
		activeAccount: String,
		postId: String,
	) {
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(activeAccount)

		val posts =
			postRemoteDataSource.fetchPost(instance, instanceType, token, postId.split('@').first())
				.unwrapQuotes()
		val profiles = posts.flatMap { it.unwrapProfiles() }.toSet()
		val emojis =
			posts.flatMap { it.emojis.values }.plus(profiles.flatMap { it.emojis.values }).toSet()
		val postEmojiCrossRefs = posts.flatMap { post ->
			post.emojis.values.map { emoji ->
				PostEmojiCrossRef(postId = post.id, fullEmojiId = emoji.fullEmojiId)
			}
		}
		val profileEmojiCrossRefs = profiles.flatMap { profile ->
			profile.emojis.values.map { emoji ->
				ProfileEmojiCrossRef(profileId = profile.id, fullEmojiId = emoji.fullEmojiId)
			}
		}

		coroutineScope {
			val emojiRef = async { emojiLocalDataSource.insertOrReplace(*emojis.toTypedArray()) }

			profileRepository.insertOrReplaceMain(*profiles.toTypedArray())
			postLocalDataSource.insertOrReplace(*posts.toTypedArray())

			emojiRef.await()

			val postEmojiRef =
				async { postLocalDataSource.insertOrReplaceEmojiCrossRef(*postEmojiCrossRefs.toTypedArray()) }
			val profileEmojiRef =
				async { profileRepository.insertOrReplaceEmojiCrossRef(*profileEmojiCrossRefs.toTypedArray()) }

			awaitAll(postEmojiRef, profileEmojiRef)
		}
	}

	suspend fun fetchPostsByProfile(
		activeAccount: String,
		profileId: String,
	) {
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(activeAccount)

		val posts =
			postRemoteDataSource.fetchPostsByProfile(
				instance,
				instanceType,
				token,
				profileId.split('@').first()
			)
				.flatMap { it.unwrapQuotes() }
		val profiles = posts.flatMap { it.unwrapProfiles() }.toSet()
		val emojis =
			posts.flatMap { it.emojis.values }.plus(profiles.flatMap { it.emojis.values }).toSet()
		val postEmojiCrossRefs = posts.flatMap { post ->
			post.emojis.values.map { emoji ->
				PostEmojiCrossRef(postId = post.id, fullEmojiId = emoji.fullEmojiId)
			}
		}
		val profileEmojiCrossRefs = profiles.flatMap { profile ->
			profile.emojis.values.map { emoji ->
				ProfileEmojiCrossRef(profileId = profile.id, fullEmojiId = emoji.fullEmojiId)
			}
		}

		coroutineScope {
			val emojiRef = async {
				emojiLocalDataSource.insertOrReplace(*emojis.toTypedArray())
			}

			profileRepository.insertOrReplaceMain(*profiles.toTypedArray())
			insertOrReplace(*posts.toTypedArray())

			emojiRef.await()

			val postEmojiRef =
				async { postLocalDataSource.insertOrReplaceEmojiCrossRef(*postEmojiCrossRefs.toTypedArray()) }
			val profileEmojiRef =
				async { profileRepository.insertOrReplaceEmojiCrossRef(*profileEmojiCrossRefs.toTypedArray()) }

			awaitAll(postEmojiRef, profileEmojiRef)
		}
	}

	suspend fun votePoll(
		activeAccount: String,
		postId: String,
		choices: List<Int>,
	) {
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(activeAccount)

		postRemoteDataSource.votePoll(
			instance,
			instanceType,
			token,
			postId.split('@').first(),
			choices
		)
	}

}
