package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.api.iceshrimp.NotificationIceshrimpApi
import sh.elizabeth.fedihome.api.iceshrimp.model.toDomain
import sh.elizabeth.fedihome.api.mastodon.NotificationMastodonApi
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.api.sharkey.NotificationSharkeyApi
import sh.elizabeth.fedihome.api.sharkey.model.toDomain
import sh.elizabeth.fedihome.model.Notification
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class NotificationRemoteDataSource @Inject constructor(
    private val notificationSharkeyApi: NotificationSharkeyApi,
    private val notificationIceshrimpApi: NotificationIceshrimpApi,
    private val notificationMastodonApi: NotificationMastodonApi,
) {
    suspend fun getNotifications(
        forAccount: String,
        instance: String,
        endpoint: String,
        instanceType: SupportedInstances,
        token: String,
    ): List<Notification> = when (instanceType) {
        SupportedInstances.SHARKEY -> notificationSharkeyApi.getNotifications(
            endpoint = endpoint, token = token
        ).map { it.toDomain(instance, forAccount) }

        SupportedInstances.ICESHRIMP -> notificationIceshrimpApi.getNotifications(
            endpoint = endpoint, token = token
        ).map { it.toDomain(instance, forAccount) }

        SupportedInstances.MASTODON, SupportedInstances.GLITCH -> notificationMastodonApi.getNotifications(
            endpoint = endpoint, token = token
        ).map { it.toDomain(instance, forAccount) }

    }
}
