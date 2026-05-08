package sh.elizabeth.fedihome.api.mastodon

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.parameters
import sh.elizabeth.fedihome.api.mastodon.model.Post
import javax.inject.Inject

class TimelineMastodonApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getHome(
		endpoint: String,
		token: String,
		maxId: String? = null,
		minId: String? = null,
		sinceId: String? = null,
		offset: Int? = null,
		limit: Int? = null,
	): List<Post> = httpClient.get("https://$endpoint/api/v1/timelines/home") {
		url {
			parameters {
				if (maxId != null) append("max_id", maxId)
				if (sinceId != null) append("since_id", sinceId)
				if (offset != null) append("offset", offset.toString())
				if (minId != null) append("min_id", minId)
				if (limit != null) append("limit", limit.toString())
			}
		}
		bearerAuth(token)
	}.body()
}
