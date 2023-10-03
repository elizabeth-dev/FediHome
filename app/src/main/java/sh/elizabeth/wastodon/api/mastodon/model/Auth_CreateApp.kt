package sh.elizabeth.wastodon.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAppRequest(
	val client_name: String,
	val redirect_uris: String,
	val scopes: String? = null,
	val website: String? = null,
)

@Serializable
data class CreateAppResponse(
	val id: String,
	val name: String,
	val website: String? = null,
	@SerialName("redirect_uri") val redirectUri: String,
	@SerialName("client_id") val clientId: String? = null,
	@SerialName("client_secret") val clientSecret: String? = null,
	@SerialName("vapid_key") val vapidKey: String,
)
