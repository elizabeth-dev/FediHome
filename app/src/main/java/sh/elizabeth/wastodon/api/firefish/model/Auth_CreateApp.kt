package sh.elizabeth.wastodon.api.firefish.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateAppRequest(
    val name: String,
    val description: String,
    val permission: List<String>,
    val callbackUrl: String? = null,
)

@Serializable
data class CreateAppResponse(
	val id: String,
	val name: String,
	val callbackUrl: String? = null,
	val permission: List<String>,
	val secret: String,
	val isAuthorized: String? = null,
)
