package sh.elizabeth.wastodon.data.datasource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import sh.elizabeth.wastodon.data.model.GetHomeRequest
import sh.elizabeth.wastodon.data.model.Post
import javax.inject.Inject

class TimelineRemoteDataSource @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getHome(instance: String): List<Post> = httpClient.post("https://$instance/api/notes/timeline") {
		contentType(ContentType.Application.Json)
		setBody(GetHomeRequest(limit = 11))
	}.body()
}
