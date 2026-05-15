package sh.elizabeth.fedihome.data.repository

import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.PostEmojiCrossRef
import sh.elizabeth.fedihome.ProfileEmojiCrossRef
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.datasource.EmojiLocalDataSource
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.data.datasource.NotificationLocalDataSource
import sh.elizabeth.fedihome.data.datasource.NotificationRemoteDataSource
import sh.elizabeth.fedihome.model.unwrapPosts
import sh.elizabeth.fedihome.model.unwrapProfiles
import sh.elizabeth.fedihome.util.InstanceEndpointTypeToken
import javax.inject.Inject

class NotificationRepository @Inject constructor(
	private val notificationLocalDataSource: NotificationLocalDataSource,
	private val notificationRemoteDataSource: NotificationRemoteDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
	private val postRepository: PostRepository,
	private val profileRepository: ProfileRepository,
	private val emojiLocalDataSource: EmojiLocalDataSource,
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

	suspend fun fetchNotifications(
		activeAccount: String,
		untilId: String? = null,
		limit: Int = 20,
	): List<String> {
		val instanceData = getInstanceAndEndpointAndTypeAndToken(activeAccount)

		val notificationRes = notificationRemoteDataSource.getNotifications(
			forAccount = activeAccount,
			instance = instanceData.instance,
			endpoint = instanceData.endpoint,
			instanceType = instanceData.instanceType,
			token = instanceData.token,
			untilId = untilId,
			limit = limit
		)

		val posts = notificationRes.mapNotNull { it.post }.flatMap {
			it.unwrapPosts()
		}.toSet()
		val profiles =
			posts
				.flatMap { it.unwrapProfiles() }
				.plus(notificationRes.mapNotNull { it.profile })
				.toSet()
		val emojis =
			posts
				.flatMap { it.emojis.values }
				.plus(profiles.flatMap { it.emojis.values })
				.plus(notificationRes.mapNotNull { it.reactionEmoji })
				.toSet()

		val postEmojiCrossRefs = posts.flatMap { post ->
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

		appDatabase.notificationQueries.transaction {
			emojiLocalDataSource.insertOrReplace(*emojis.toTypedArray())
			profileRepository.insertOrReplace(*profiles.toTypedArray())
			postRepository.insertOrReplace(*posts.toTypedArray())
			notificationLocalDataSource.insertOrReplace(*notificationRes.toTypedArray())
			postRepository.insertOrReplaceEmojiCrossRef(*postEmojiCrossRefs.toTypedArray())
			profileRepository.insertOrReplaceEmojiCrossRef(*profileEmojiCrossRefs.toTypedArray())
			notificationLocalDataSource.insertNotificationPagingItems(*notificationRes.toTypedArray())
		}

		return notificationRes.map { it.id }
	}
//
//	fun getNotificationPagingSource(forAccount: String) =
//		notificationLocalDataSource.getNotificationPagingSource(forAccount)
}
