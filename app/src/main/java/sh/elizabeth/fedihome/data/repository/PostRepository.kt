package sh.elizabeth.fedihome.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.PostEmojiCrossRef
import sh.elizabeth.fedihome.ProfileEmojiCrossRef
import sh.elizabeth.fedihome.data.datasource.EmojiLocalDataSource
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.data.datasource.PostLocalDataSource
import sh.elizabeth.fedihome.data.datasource.PostRemoteDataSource
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.PostDraft
import sh.elizabeth.fedihome.model.unwrapProfiles
import sh.elizabeth.fedihome.model.unwrapQuotes
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class PostRepository @Inject constructor(
	private val postLocalDataSource: PostLocalDataSource,
	private val profileRepository: ProfileRepository,
	private val emojiLocalDataSource: EmojiLocalDataSource,
	private val postRemoteDataSource: PostRemoteDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {
	private suspend fun getInstanceAndTypeAndToken(activeAccount: String):
			Triple<String, SupportedInstances, String> =
		activeAccount.let {
			val internalData = internalDataLocalDataSource.internalData.first()
			val instance = it.split('@')[1]
			Triple(
				instance,
				internalData.serverTypes[instance]!!,
				internalData.accessTokens[it]!!
			)
		}

	fun insertOrReplace(vararg posts: Post) {
		postLocalDataSource.insertOrReplace(*posts)
	}

	fun insertOrReplaceEmojiCrossRef(vararg refs: PostEmojiCrossRef) {
		postLocalDataSource.insertOrReplaceEmojiCrossRef(*refs)
	}

	suspend fun createPost(activeAccount: String, newPost: PostDraft) {
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(
			activeAccount
		)

		val postRes = postRemoteDataSource.createPost(
			instance, instanceType, token, newPost
		)
		insertOrReplace(postRes)
	}

	fun getPost(postId: String): Post? =
		postLocalDataSource.getPostSingle(postId)

	fun getPostFlow(postId: String) = postLocalDataSource.getPost(postId)

	fun getPostsByProfileFlow(profileId: String) =
		postLocalDataSource.getPostsByProfile(profileId)

	suspend fun fetchPost(
		activeAccount: String,
		postId: String,
	) {
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(
			activeAccount
		)

		val posts = postRemoteDataSource.fetchPost(
			instance, instanceType, token, postId.split('@').first()
		).unwrapQuotes()
		val profiles = posts.flatMap { it.unwrapProfiles() }.toSet()
		val emojis =
			posts.flatMap { it.emojis.values }
				.plus(profiles.flatMap { it.emojis.values })
				.toSet()
		val postEmojiCrossRefs = posts.flatMap { post ->
			post.emojis.values.map { emoji ->
				PostEmojiCrossRef(
					postId = post.id, emojiId = emoji.fullEmojiId
				)
			}
		}
		val profileEmojiCrossRefs = profiles.flatMap { profile ->
			profile.emojis.values.map { emoji ->
				ProfileEmojiCrossRef(
					profileId = profile.id, emojiId = emoji.fullEmojiId
				)
			}
		}

		coroutineScope {
			val emojiRef =
				async { emojiLocalDataSource.insertOrReplace(*emojis.toTypedArray()) }

			profileRepository.insertOrReplace(*profiles.toTypedArray())
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
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(
			activeAccount
		)

		val posts = postRemoteDataSource.fetchPostsByProfile(
			instance, instanceType, token, profileId.split('@').first()
		).flatMap { it.unwrapQuotes() }
		val profiles = posts.flatMap { it.unwrapProfiles() }.toSet()
		val emojis =
			posts.flatMap { it.emojis.values }
				.plus(profiles.flatMap { it.emojis.values })
				.toSet()
		val postEmojiCrossRefs = posts.flatMap { post ->
			post.emojis.values.map { emoji ->
				PostEmojiCrossRef(
					postId = post.id, emojiId = emoji.fullEmojiId
				)
			}
		}
		val profileEmojiCrossRefs = profiles.flatMap { profile ->
			profile.emojis.values.map { emoji ->
				ProfileEmojiCrossRef(
					profileId = profile.id, emojiId = emoji.fullEmojiId
				)
			}
		}

		coroutineScope {
			val emojiRef = async {
				emojiLocalDataSource.insertOrReplace(*emojis.toTypedArray())
			}

			profileRepository.insertOrReplace(*profiles.toTypedArray())
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
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(
			activeAccount
		)

		postRemoteDataSource.votePoll(
			instance, instanceType, token, postId.split('@').first(), choices
		)
	}

}
