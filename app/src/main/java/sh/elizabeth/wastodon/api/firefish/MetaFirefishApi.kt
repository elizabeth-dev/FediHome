package sh.elizabeth.wastodon.api.firefish

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import javax.inject.Inject

class MetaFirefishApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getPing(instance: String): Boolean =
		httpClient.post("https://$instance/api/ping").status == HttpStatusCode.OK

}
