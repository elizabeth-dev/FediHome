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
import javax.inject.Inject

class NotificationSharkeyApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getNotifications(instance: String, token: String): List<Notification> = httpClient.post("https://$instance/api/i/notifiications") {
		contentType(ContentType.Application.Json)
		bearerAuth(token)
		setBody(GetNotificationsRequest())
	}.body()
}
