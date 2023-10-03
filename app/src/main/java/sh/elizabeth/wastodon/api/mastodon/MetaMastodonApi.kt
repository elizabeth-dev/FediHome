package sh.elizabeth.wastodon.api.mastodon

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import sh.elizabeth.wastodon.api.mastodon.model.InstanceResponse
import javax.inject.Inject

class MetaMastodonApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getInstance(instance: String): InstanceResponse? =
		httpClient.get("https://$instance/api/v2/instance")
			.let { if (it.status == HttpStatusCode.OK) it.body() else null }
}
