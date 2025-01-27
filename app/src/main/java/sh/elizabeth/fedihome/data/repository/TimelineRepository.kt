package sh.elizabeth.fedihome.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.PostEmojiCrossRef
import sh.elizabeth.fedihome.ProfileEmojiCrossRef
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.datasource.EmojiLocalDataSource
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.data.datasource.TimelineLocalDataSource
import sh.elizabeth.fedihome.data.datasource.TimelinePost
import sh.elizabeth.fedihome.data.datasource.TimelineRemoteDataSource
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.unwrapProfiles
import sh.elizabeth.fedihome.model.unwrapQuotes
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

	fun getTimeline(profileIdentifier: String): Flow<List<Post>> =
		timelineLocalDataSource.getTimelinePosts(profileIdentifier)

	suspend fun fetchTimeline(
		activeAccount: String,
		profileIdentifier: String,
	) {
		val instanceData = getInstanceAndEndpointAndTypeAndToken(activeAccount)

		val posts = timelineRemoteDataSource.getHome(
			instance = instanceData.instance,
			endpoint = instanceData.endpoint,
			instanceType = instanceData.instanceType,
			token = instanceData.token
		)
		val unWrappedPosts = posts.flatMap { it.unwrapQuotes() }
		val profiles = unWrappedPosts.flatMap { it.unwrapProfiles() }.toSet()
		val emojis =
			unWrappedPosts.flatMap { it.emojis.values }.plus(profiles.flatMap { it.emojis.values })
				.toSet()

		val postEmojiCrossRefs = unWrappedPosts.flatMap { post ->
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


		appDatabase.transaction {
			emojiLocalDataSource.insertOrReplace(*emojis.toTypedArray())

			profileRepository.insertOrReplace(*profiles.toTypedArray())
			postRepository.insertOrReplace(*unWrappedPosts.toTypedArray())

			timelineLocalDataSource.insert(
				profileIdentifier, *posts.map {
					TimelinePost(
						postId = it.id, repostedById = it.repostedBy?.id
					)
				}.reversed().toTypedArray()
			)

			postRepository.insertOrReplaceEmojiCrossRef(*postEmojiCrossRefs.toTypedArray())
			profileRepository.insertOrReplaceEmojiCrossRef(*profileEmojiCrossRefs.toTypedArray())
		}
	}
}
