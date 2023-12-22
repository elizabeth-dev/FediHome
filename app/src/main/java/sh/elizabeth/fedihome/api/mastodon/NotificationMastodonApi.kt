package sh.elizabeth.fedihome.api.mastodon

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import sh.elizabeth.fedihome.api.mastodon.model.Notification
import javax.inject.Inject

class NotificationMastodonApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getNotifications(instance: String, token: String): List<Notification> =
		httpClient.get("https://$instance/api/v1/notifications") {
			bearerAuth(token)
		}.body()
}
