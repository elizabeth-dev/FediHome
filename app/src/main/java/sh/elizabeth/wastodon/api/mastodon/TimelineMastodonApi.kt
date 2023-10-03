package sh.elizabeth.wastodon.api.mastodon

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import sh.elizabeth.wastodon.api.mastodon.model.Post
import javax.inject.Inject

class TimelineMastodonApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getHome(instance: String, token: String): List<Post> =
		httpClient.get("https://$instance/api/v1/timelines/home") { bearerAuth(token) }.body()
}
