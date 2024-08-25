package sh.elizabeth.fedihome.api.mastodon

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import sh.elizabeth.fedihome.MainDestinations
import sh.elizabeth.fedihome.api.mastodon.model.AccessTokenRequest
import sh.elizabeth.fedihome.api.mastodon.model.AccessTokenResponse
import sh.elizabeth.fedihome.api.mastodon.model.CreateAppRequest
import sh.elizabeth.fedihome.api.mastodon.model.CreateAppResponse
import sh.elizabeth.fedihome.api.mastodon.model.Profile
import sh.elizabeth.fedihome.util.APP_DEEPLINK_URI
import sh.elizabeth.fedihome.util.APP_NAME
import sh.elizabeth.fedihome.util.MASTODON_APP_PERMISSION
import javax.inject.Inject

class AuthMastodonApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun createApp(
		instance: String,
		name: String = APP_NAME,
		callbackUrl: String = "$APP_DEEPLINK_URI/${MainDestinations.LOGIN_OAUTH_ROUTE}",
		scopes: List<String>? = MASTODON_APP_PERMISSION,
		website: String? = null,
	): CreateAppResponse = httpClient.post("https://$instance/api/v1/apps") {
		contentType(ContentType.Application.Json)
		setBody(
			CreateAppRequest(
				client_name = name,
				redirect_uris = callbackUrl,
				scopes = scopes?.joinToString(" "),
				website = website
			)
		)
	}.body()

	suspend fun getAccessToken(
		instance: String,
		code: String,
		clientId: String,
		clientSecret: String,
		callbackUrl: String = "$APP_DEEPLINK_URI/${MainDestinations.LOGIN_OAUTH_ROUTE}",
		scopes: List<String>? = MASTODON_APP_PERMISSION,
	): AccessTokenResponse = httpClient.post("https://$instance/oauth/token") {
		contentType(ContentType.Application.Json)
		setBody(
			AccessTokenRequest(
				code = code,
				client_id = clientId,
				client_secret = clientSecret,
				redirect_uri = callbackUrl,
				scope = scopes?.joinToString(" ")
			)
		)
	}.body()

	suspend fun verifyCredentials(instance: String, accessToken: String?): Profile =
		httpClient.get("https://$instance/api/v1/accounts/verify_credentials") {
			if (accessToken != null) header(HttpHeaders.Authorization, "Bearer $accessToken")
		}.body()
}
