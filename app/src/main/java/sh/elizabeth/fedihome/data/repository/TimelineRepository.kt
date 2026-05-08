package sh.elizabeth.fedihome.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.PostEmojiCrossRef
import sh.elizabeth.fedihome.ProfileEmojiCrossRef
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.datasource.EmojiLocalDataSource
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.data.datasource.TimelineLocalDataSource
import sh.elizabeth.fedihome.data.datasource.TimelineRemoteDataSource
import sh.elizabeth.fedihome.model.Emoji
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.unwrapPosts
import sh.elizabeth.fedihome.model.unwrapProfiles
import sh.elizabeth.fedihome.util.InstanceEndpointTypeToken
import javax.inject.Inject

class TimelineRepository @Inject constructor(
	private val timelineLocalDataSource: TimelineLocalDataSource,
	private val postRepository: PostRepository,
	private val profileRepository: ProfileRepository,
	private val emojiLocalDataSource: EmojiLocalDataSource,
	private val timelineRemoteDataSource: TimelineRemoteDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
	private val appDatabase: AppDatabase,
) {
	private suspend fun getInstanceAndEndpointAndTypeAndToken(activeAccount: String): InstanceEndpointTypeToken =
		activeAccount.let {
			val internalData = internalDataLocalDataSource.internalData.first()
			val instance = it.split('@')[1]
			InstanceEndpointTypeToken(
				instance,
				internalData.instances[instance]?.delegatedEndpoint!!,
				internalData.instances[instance]?.instanceType!!,
				internalData.accounts[it]?.accessToken!!
			)
		}

	fun getTimeline(
		profileIdentifier: String,
		limit: Long = 20,
		offset: Long = 0,
	): Flow<List<Post>> = timelineLocalDataSource.getTimelinePosts(profileIdentifier, limit, offset)

	suspend fun fetchTimeline(
		activeAccount: String,
		profileIdentifier: String,
		untilId: String? = null,
		limit: Int = 20,
	): List<String> {
		val instanceData = getInstanceAndEndpointAndTypeAndToken(activeAccount)

		val posts = timelineRemoteDataSource.getHome(
			instance = instanceData.instance,
			endpoint = instanceData.endpoint,
			instanceType = instanceData.instanceType,
			token = instanceData.token,
			untilId = untilId,
			limit = limit
		)
		val unwrappedPosts = posts.flatMap { it.unwrapPosts() }
		val profiles = unwrappedPosts.flatMap { it.unwrapProfiles() }
		val emojis =
			unwrappedPosts
				.flatMap { it.emojis.values }
				.plus(profiles.flatMap { it.emojis.values })
				.distinctBy(Emoji::fullEmojiId)

		val postEmojiCrossRefs = unwrappedPosts.flatMap { post ->
			post.emojis.values.map { emoji ->
				PostEmojiCrossRef(postId = post.id, emojiId = emoji.fullEmojiId)
			}
		}
		val profileEmojiCrossRefs = profiles.flatMap { profile ->
			profile.emojis.values.map { emoji ->
				ProfileEmojiCrossRef(
					profileId = profile.id, emojiId = emoji.fullEmojiId
				)
			}
		}


		// TODO: make foreign keys deferrable and make this async
		appDatabase.transaction {
			emojiLocalDataSource.insertOrReplace(*emojis.toTypedArray())

			profileRepository.insertOrReplace(*profiles.toTypedArray())
			postRepository.insertOrReplace(*unwrappedPosts.toTypedArray())

			timelineLocalDataSource.insert(
				profileIdentifier, *posts.map {
					it.id
				}.reversed().toTypedArray()
			)

			postRepository.insertOrReplaceEmojiCrossRef(*postEmojiCrossRefs.toTypedArray())
			profileRepository.insertOrReplaceEmojiCrossRef(*profileEmojiCrossRefs.toTypedArray())
		}

		return posts.map { it.id }
	}

	suspend fun fetchTimelineWithBackfill(
		activeAccount: String,
		profileIdentifier: String,
		limit: Int = 20,
		maxBackfillPages: Int = 5,
	) {
		var fetchedIds = fetchTimeline(
			activeAccount = activeAccount, profileIdentifier = profileIdentifier, limit = limit
		)

		var pagesLoaded = 1
		while (fetchedIds.isNotEmpty() && pagesLoaded < maxBackfillPages) {
			val oldestFetchedId = fetchedIds.last()

			if (timelineLocalDataSource.existsInTimeline(profileIdentifier, oldestFetchedId)) break

			fetchedIds = fetchTimeline(
				activeAccount = activeAccount,
				profileIdentifier = profileIdentifier,
				untilId = oldestFetchedId,
				limit = limit
			)
			pagesLoaded++
		}
	}
}
