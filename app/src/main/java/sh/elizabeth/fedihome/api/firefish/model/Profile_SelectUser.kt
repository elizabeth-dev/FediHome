package sh.elizabeth.fedihome.api.firefish.model

import kotlinx.serialization.Serializable

@Serializable
data class SelectUserRequest(
	val userId: String,
)
