package sh.elizabeth.wastodon.api.firefish

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.wastodon.MainDestinations
import sh.elizabeth.wastodon.api.firefish.model.CreateAppRequest
import sh.elizabeth.wastodon.api.firefish.model.CreateAppResponse
import sh.elizabeth.wastodon.api.firefish.model.GenerateSessionRequest
import sh.elizabeth.wastodon.api.firefish.model.GenerateSessionResponse
import sh.elizabeth.wastodon.api.firefish.model.UserKeyRequest
import sh.elizabeth.wastodon.api.firefish.model.UserKeyResponse
import sh.elizabeth.wastodon.util.APP_DEEPLINK_URI
import sh.elizabeth.wastodon.util.APP_DESCRIPTION
import sh.elizabeth.wastodon.util.APP_NAME
import sh.elizabeth.wastodon.util.FIREFISH_APP_PERMISSION
import javax.inject.Inject

class AuthFirefishApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun createApp(
		instance: String,
		name: String = APP_NAME,
		description: String = APP_DESCRIPTION,
		permission: List<String> = FIREFISH_APP_PERMISSION,
		callbackUrl: String = "$APP_DEEPLINK_URI/${MainDestinations.LOGIN_ROUTE}",
	): CreateAppResponse = httpClient.post("https://$instance/api/app/create") {
		contentType(ContentType.Application.Json)
		setBody(
			CreateAppRequest(
				name = name,
				description = description,
				permission = permission,
				callbackUrl = callbackUrl
			)
		)
	}.body()

	suspend fun generateSession(instance: String, appSecret: String): GenerateSessionResponse =
		httpClient.post(
			"https://$instance/api/auth/session/generate"
		) {
			contentType(ContentType.Application.Json)
			setBody(GenerateSessionRequest(appSecret = appSecret))
		}.body()

	suspend fun getUserKey(instance: String, appSecret: String, token: String): UserKeyResponse =
		httpClient.post(
			"https://$instance/api/auth/session/userkey"
		) {
			contentType(ContentType.Application.Json)
			setBody(UserKeyRequest(appSecret = appSecret, token = token))
		}.body()
}
