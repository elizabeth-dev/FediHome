package sh.elizabeth.fedihome.api.firefish

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.fedihome.api.firefish.model.GetHomeRequest
import sh.elizabeth.fedihome.api.firefish.model.Post
import javax.inject.Inject

class TimelineFirefishApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getHome(instance: String, token: String): List<Post> =
		httpClient.post("https://$instance/api/notes/timeline") {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(GetHomeRequest(limit = 11))
		}.body()
}
