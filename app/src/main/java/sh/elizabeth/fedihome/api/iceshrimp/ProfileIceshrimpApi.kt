package sh.elizabeth.fedihome.api.iceshrimp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.fedihome.api.iceshrimp.model.SelectUserRequest
import sh.elizabeth.fedihome.api.iceshrimp.model.UserDetailedNotMe
import javax.inject.Inject

class ProfileIceshrimpApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun fetchProfile(
		endpoint: String,
		token: String,
		profileId: String,
	): UserDetailedNotMe = httpClient.post("https://$endpoint/api/users/show") {
		contentType(ContentType.Application.Json)
		bearerAuth(token)
		setBody(SelectUserRequest(profileId.split('@').first()))
	}.body()
}
