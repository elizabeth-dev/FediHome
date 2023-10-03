package sh.elizabeth.fedihome.api.mastodon

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import sh.elizabeth.fedihome.api.mastodon.model.Profile
import javax.inject.Inject

class ProfileMastodonApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun fetchProfile(instance: String, token: String, profileId: String): Profile =
		httpClient.get("https://$instance/api/v1/accounts/$profileId") { bearerAuth(token) }.body()
}
