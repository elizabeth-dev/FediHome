package sh.elizabeth.fedihome.api.mastodon

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.fedihome.api.mastodon.model.Notification
import sh.elizabeth.fedihome.api.mastodon.model.PushSubscriptionRequest
import sh.elizabeth.fedihome.api.mastodon.model.PushSubscriptionRequest_Alerts
import sh.elizabeth.fedihome.api.mastodon.model.PushSubscriptionRequest_Data
import sh.elizabeth.fedihome.api.mastodon.model.PushSubscriptionRequest_Keys
import sh.elizabeth.fedihome.api.mastodon.model.PushSubscriptionRequest_Policy
import sh.elizabeth.fedihome.api.mastodon.model.PushSubscriptionRequest_Subscription
import sh.elizabeth.fedihome.api.mastodon.model.PushSubscriptionResponse
import javax.inject.Inject

class NotificationMastodonApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getNotifications(
		endpoint: String,
		token: String,
	): List<Notification> =
		httpClient.get("https://$endpoint/api/v1/notifications") {
			bearerAuth(token)
		}.body()

	suspend fun createPushSubscription(
		instance: String,
		token: String,
		deviceToken: String,
		pushAccountId: String,
		publicKey: String,
		authKey: String,
	): PushSubscriptionResponse =
		httpClient.post("https://$instance/api/v1/push/subscription") {
			bearerAuth(token)
			contentType(ContentType.Application.Json)
			setBody(
				PushSubscriptionRequest(
					subscription = PushSubscriptionRequest_Subscription(
						endpoint = "https://api.fedihome.elizabeth.sh/push/fcm/mastodon/$deviceToken/$pushAccountId",
						keys = PushSubscriptionRequest_Keys(
							p256dh = publicKey,
							auth = authKey,
						),
					),
					data = PushSubscriptionRequest_Data(
						alerts = PushSubscriptionRequest_Alerts.ALL,
						policy = PushSubscriptionRequest_Policy.ALL
					),
				)
			)
		}.body()

	suspend fun deletePushSubscription(instance: String, token: String) {
		httpClient.delete("https://$instance/api/v1/push/subscription") {
			bearerAuth(token)
		}
	}
}
