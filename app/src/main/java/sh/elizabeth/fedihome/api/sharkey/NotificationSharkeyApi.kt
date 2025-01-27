package sh.elizabeth.fedihome.api.sharkey

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.fedihome.api.sharkey.model.GetNotificationsRequest
import sh.elizabeth.fedihome.api.sharkey.model.Notification
import sh.elizabeth.fedihome.api.sharkey.model.PushNotification_Subscription_Request
import sh.elizabeth.fedihome.api.sharkey.model.PushNotification_Subscription_Response
import sh.elizabeth.fedihome.api.sharkey.model.PushNotification_Unregister
import javax.inject.Inject

class NotificationSharkeyApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getNotifications(
		endpoint: String,
		token: String,
	): List<Notification> =
		httpClient.post("https://$endpoint/api/i/notifiications") {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(GetNotificationsRequest())
		}.body()

	suspend fun createPushSubscription(
		instance: String,
		token: String,
		deviceToken: String,
		pushAccountId: String,
		publicKey: String,
		authSecret: String,
		sendReadMessage: Boolean = false,
	): PushNotification_Subscription_Response =
		httpClient.post("https://$instance/api/sw/register") {
			contentType(ContentType.Application.Json)
			setBody(
				PushNotification_Subscription_Request(
					endpoint = "https://api.fedihome.elizabeth.sh/push/fcm/key/$deviceToken/$pushAccountId",
					publickey = publicKey,
					auth = authSecret,
					sendReadMessage = sendReadMessage,
					i = token,
				)
			)
		}.body()

	suspend fun deletePushSubscription(
		instance: String,
		token: String,
		endpoint: String,
	) {
		httpClient.post("https://$instance/api/sw/unregister") {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(PushNotification_Unregister(endpoint))
		}
	}
}
