package sh.elizabeth.wastodon.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import sh.elizabeth.wastodon.data.database.entity.PostEmojiCrossRef
import sh.elizabeth.wastodon.data.database.entity.ProfileEmojiCrossRef
import sh.elizabeth.wastodon.data.datasource.EmojiLocalDataSource
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
	private val emojiLocalDataSource: EmojiLocalDataSource,
) {
	suspend fun insertOrReplace(vararg posts: Post) {
		postLocalDataSource.insertOrReplace(*posts)
	}

	suspend fun insertOrReplaceEmojiCrossRef(vararg refs: PostEmojiCrossRef) {
		postLocalDataSource.insertOrReplaceEmojiCrossRef(*refs)
	}

	suspend fun createPost(instance: String, newPost: PostDraft) {
		val postRes = postRemoteDataSource.createPost(instance, newPost)
		insertOrReplace(postRes.createdNote.toDomain(instance))
	}

	suspend fun getPost(postId: String): Post? = postLocalDataSource.getPost(postId)

	fun getPostFlow(postId: String) = postLocalDataSource.getPostFlow(postId)

	fun getPostsByProfileFlow(profileId: String) =
		postLocalDataSource.getPostsByProfileFlow(profileId)

	suspend fun fetchPost(
		instance: String,
		postId: String,
	) {
		val posts =
			postRemoteDataSource.fetchPost(instance, postId).toDomain(instance).unwrapQuotes()
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

	suspend fun fetchPostsByProfile(instance: String, profileId: String) {
		val posts = postRemoteDataSource.fetchPostsByProfile(instance, profileId)
			.map { it.toDomain(instance) }
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

	suspend fun votePoll(instance: String, postId: String, choice: Int) =
		postRemoteDataSource.votePoll(instance, postId, choice)

}
