package sh.elizabeth.wastodon.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.wastodon.data.model.SelectUserRequest
import sh.elizabeth.wastodon.data.model.UserDetailedNotMe
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(private val httpClient: HttpClient) {
	suspend fun fetchProfile(instance: String, profileId: String): UserDetailedNotMe =
		httpClient.post("https://$instance/api/users/show") {
			contentType(ContentType.Application.Json)
			setBody(SelectUserRequest(profileId.split('@').first()))
		}.body()
}
