package sh.elizabeth.wastodon.api.mastodon.model

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenRequest(
	val grant_type: String = "authorization_code",
	val code: String,
	val client_id: String,
	val client_secret: String,
	val redirect_uri: String,
	val scope: String? = null,
)

@Serializable
data class AccessTokenResponse(
	val access_token: String,
	val token_type: String,
	val scope: String,
	val created_at: Long,
)
