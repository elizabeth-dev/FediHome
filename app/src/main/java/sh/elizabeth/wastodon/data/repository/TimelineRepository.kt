package sh.elizabeth.wastodon.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import sh.elizabeth.wastodon.data.database.entity.PostEmojiCrossRef
import sh.elizabeth.wastodon.data.database.entity.ProfileEmojiCrossRef
import sh.elizabeth.wastodon.data.datasource.EmojiLocalDataSource
import sh.elizabeth.wastodon.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.wastodon.data.datasource.TimelineLocalDataSource
import sh.elizabeth.wastodon.data.datasource.TimelinePost
import sh.elizabeth.wastodon.data.datasource.TimelineRemoteDataSource
import sh.elizabeth.wastodon.model.Post
import sh.elizabeth.wastodon.model.unwrapProfiles
import sh.elizabeth.wastodon.model.unwrapQuotes
import sh.elizabeth.wastodon.util.SupportedInstances
import javax.inject.Inject

class TimelineRepository @Inject constructor(
	private val timelineLocalDataSource: TimelineLocalDataSource,
	private val postRepository: PostRepository,
	private val profileRepository: ProfileRepository,
	private val emojiLocalDataSource: EmojiLocalDataSource,
	private val timelineRemoteDataSource: TimelineRemoteDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {
	private suspend fun getInstanceAndTypeAndToken(activeAccount: String): Triple<String, SupportedInstances, String> =
		activeAccount.let {
			val internalData = internalDataLocalDataSource.internalData.first()
			val instance = it.split(':').first()
			Triple(instance, internalData.serverTypes[instance]!!, internalData.accessTokens[it]!!)
		}

	fun getTimeline(profileIdentifier: String): Flow<List<Post>> =
		timelineLocalDataSource.getTimelinePosts(profileIdentifier)

	suspend fun fetchTimeline(
		activeAccount: String,
		profileIdentifier: String,
	) {
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(activeAccount)

		val posts = timelineRemoteDataSource.getHome(instance, instanceType, token)
		val unWrappedPosts = posts.flatMap { it.unwrapQuotes() }
		val profiles = unWrappedPosts.flatMap { it.unwrapProfiles() }.toSet()
		val emojis = unWrappedPosts.flatMap { it.emojis.values }
			.plus(profiles.flatMap { it.emojis.values })
			.toSet()

		val postEmojiCrossRefs = unWrappedPosts.flatMap { post ->
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
			postRepository.insertOrReplace(*unWrappedPosts.toTypedArray())

			val timelineRef = async {
				timelineLocalDataSource.insert(profileIdentifier,
					*posts.map { TimelinePost(postId = it.id, repostedById = it.repostedBy?.id) }
						.reversed()
						.toTypedArray())
			}

			emojiRef.await()

			val postEmojiRef =
				async { postRepository.insertOrReplaceEmojiCrossRef(*postEmojiCrossRefs.toTypedArray()) }
			val profileEmojiRef =
				async { profileRepository.insertOrReplaceEmojiCrossRef(*profileEmojiCrossRefs.toTypedArray()) }

			awaitAll(timelineRef, postEmojiRef, profileEmojiRef)
		}
	}
}
