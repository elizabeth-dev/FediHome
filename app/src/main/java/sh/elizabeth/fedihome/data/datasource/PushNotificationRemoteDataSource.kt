package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.api.firefish.NotificationFirefishApi
import sh.elizabeth.fedihome.api.mastodon.NotificationMastodonApi
import sh.elizabeth.fedihome.api.sharkey.NotificationSharkeyApi
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class PushNotificationRemoteDataSource @Inject constructor(
	private val notificationSharkeyApi: NotificationSharkeyApi,
	private val notificationFirefishApi: NotificationFirefishApi,
	private val notificationMastodonApi: NotificationMastodonApi,
) {
	suspend fun registerPushSubscription(
		instance: String,
		instanceType: SupportedInstances,
		token: String,
		deviceToken: String,
		pushAccountId: String,
		publicKey: String,
		authKey: String,
	) = when (instanceType) {
		SupportedInstances.FIREFISH, SupportedInstances.SHARKEY -> TODO()
		SupportedInstances.GLITCH, SupportedInstances.MASTODON -> notificationMastodonApi.createPushSubscription(
			instance, token, deviceToken, pushAccountId, publicKey, authKey
		)
	}

	suspend fun deletePushSubscription(
		instance: String,
		instanceType: SupportedInstances,
		token: String,
	) = when (instanceType) {
		SupportedInstances.FIREFISH, SupportedInstances.SHARKEY -> TODO()
		SupportedInstances.GLITCH, SupportedInstances.MASTODON -> notificationMastodonApi.deletePushSubscription(
			instance, token
		)
	}
}