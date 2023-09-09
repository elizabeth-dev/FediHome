package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SelectUserRequest(
	val userId: String,
)
