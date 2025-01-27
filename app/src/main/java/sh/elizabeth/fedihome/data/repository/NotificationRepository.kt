package sh.elizabeth.fedihome.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.PostEmojiCrossRef
import sh.elizabeth.fedihome.ProfileEmojiCrossRef
import sh.elizabeth.fedihome.data.datasource.EmojiLocalDataSource
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.data.datasource.NotificationLocalDataSource
import sh.elizabeth.fedihome.data.datasource.NotificationRemoteDataSource
import sh.elizabeth.fedihome.model.unwrapProfiles
import sh.elizabeth.fedihome.model.unwrapQuotes
import sh.elizabeth.fedihome.util.InstanceEndpointTypeToken
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class NotificationRepository @Inject constructor(
	private val notificationLocalDataSource: NotificationLocalDataSource,
	private val notificationRemoteDataSource: NotificationRemoteDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
	private val postRepository: PostRepository,
	private val profileRepository: ProfileRepository,
	private val emojiLocalDataSource: EmojiLocalDataSource,
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

	suspend fun fetchNotifications(activeAccount: String) {
		val instanceData = getInstanceAndEndpointAndTypeAndToken(
			activeAccount
		)

		val notificationRes = notificationRemoteDataSource.getNotifications(
            forAccount = activeAccount,
            instance = instanceData.instance,
            endpoint = instanceData.endpoint,
            instanceType = instanceData.instanceType,
            token = instanceData.token
		)

		val posts = notificationRes.mapNotNull { it.post }.flatMap {
			it.unwrapQuotes()
		}.toSet()
		val profiles =
			posts.flatMap { it.unwrapProfiles() }
				.plus(notificationRes.mapNotNull { it.profile })
				.toSet()
		val emojis =
			posts.flatMap { it.emojis.values }
				.plus(profiles.flatMap { it.emojis.values })
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

		coroutineScope {
			val emojiRef =
				async { emojiLocalDataSource.insertOrReplace(*emojis.toTypedArray()) }

			profileRepository.insertOrReplace(*profiles.toTypedArray())
			postRepository.insertOrReplace(*posts.toTypedArray())

			val notificationRef = async {
				notificationLocalDataSource.insertOrReplace(*notificationRes.toTypedArray())
			}

			emojiRef.await()

			val postEmojiRef =
				async { postRepository.insertOrReplaceEmojiCrossRef(*postEmojiCrossRefs.toTypedArray()) }
			val profileEmojiRef =
				async { profileRepository.insertOrReplaceEmojiCrossRef(*profileEmojiCrossRefs.toTypedArray()) }

			awaitAll(notificationRef, postEmojiRef, profileEmojiRef)
		}
	}

	fun getNotificationsFlow(activeAccount: String) =
		notificationLocalDataSource.getNotificationsFlow(activeAccount)

}
