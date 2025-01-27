package sh.elizabeth.fedihome.api.iceshrimp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.fedihome.api.iceshrimp.model.CreateAppRequest
import sh.elizabeth.fedihome.api.iceshrimp.model.CreateAppResponse
import sh.elizabeth.fedihome.api.iceshrimp.model.GenerateSessionRequest
import sh.elizabeth.fedihome.api.iceshrimp.model.GenerateSessionResponse
import sh.elizabeth.fedihome.api.iceshrimp.model.UserKeyRequest
import sh.elizabeth.fedihome.api.iceshrimp.model.UserKeyResponse
import sh.elizabeth.fedihome.util.APP_DEEPLINK_URI
import sh.elizabeth.fedihome.util.APP_DESCRIPTION
import sh.elizabeth.fedihome.util.APP_LOGIN_OAUTH_PATH
import sh.elizabeth.fedihome.util.APP_NAME
import sh.elizabeth.fedihome.util.ICESHRIMP_APP_PERMISSION
import javax.inject.Inject

class AuthIceshrimpApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun createApp(
		endpoint: String,
		name: String = APP_NAME,
		description: String = APP_DESCRIPTION,
		permission: List<String> = ICESHRIMP_APP_PERMISSION,
		callbackUrl: String = "$APP_DEEPLINK_URI$APP_LOGIN_OAUTH_PATH",
	): CreateAppResponse = httpClient.post("https://$endpoint/api/app/create") {
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

	suspend fun generateSession(
		endpoint: String,
		appSecret: String,
	): GenerateSessionResponse =
		httpClient.post(
			"https://$endpoint/api/auth/session/generate"
		) {
			contentType(ContentType.Application.Json)
			setBody(GenerateSessionRequest(appSecret = appSecret))
		}.body()

	suspend fun getUserKey(
		endpoint: String,
		appSecret: String,
		token: String,
	): UserKeyResponse =
		httpClient.post(
			"https://$endpoint/api/auth/session/userkey"
		) {
			contentType(ContentType.Application.Json)
			setBody(UserKeyRequest(appSecret = appSecret, token = token))
		}.body()
}
