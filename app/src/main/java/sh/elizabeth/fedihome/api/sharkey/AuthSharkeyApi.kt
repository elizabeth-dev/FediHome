package sh.elizabeth.fedihome.api.sharkey

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.fedihome.api.sharkey.model.UserKeyRequest
import sh.elizabeth.fedihome.api.sharkey.model.UserKeyResponse
import javax.inject.Inject

class AuthSharkeyApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getUserKey(instance: String, appSecret: String, token: String): UserKeyResponse =
		httpClient.post(
			"https://$instance/api/auth/session/userkey"
		) {
			contentType(ContentType.Application.Json)
			setBody(UserKeyRequest(appSecret = appSecret, token = token))
		}.body()
}
