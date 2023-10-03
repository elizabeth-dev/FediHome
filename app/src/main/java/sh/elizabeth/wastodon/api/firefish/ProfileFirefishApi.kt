package sh.elizabeth.wastodon.api.firefish

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.wastodon.api.firefish.model.SelectUserRequest
import sh.elizabeth.wastodon.api.firefish.model.UserDetailedNotMe
import javax.inject.Inject

class ProfileFirefishApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun fetchProfile(
		instance: String,
		token: String,
		profileId: String,
	): UserDetailedNotMe = httpClient.post("https://$instance/api/users/show") {
		contentType(ContentType.Application.Json)
		bearerAuth(token)
		setBody(SelectUserRequest(profileId.split('@').first()))
	}.body()
}
