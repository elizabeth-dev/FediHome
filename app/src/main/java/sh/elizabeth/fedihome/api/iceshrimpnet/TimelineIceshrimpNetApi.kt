package sh.elizabeth.fedihome.api.iceshrimpnet

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.fedihome.api.iceshrimp.model.GetHomeRequest
import sh.elizabeth.fedihome.api.iceshrimp.model.Post
import javax.inject.Inject

class TimelineIceshrimpNetApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getHome(
		endpoint: String,
		token: String,
	): List<Post> = httpClient.post("https://$endpoint/api/iceshrimp/timelines/home") {
		contentType(ContentType.Application.Json)
		bearerAuth(token)
		setBody(GetHomeRequest(limit = 11))
	}.body()
}