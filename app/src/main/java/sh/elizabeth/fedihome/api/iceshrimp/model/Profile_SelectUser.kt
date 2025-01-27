package sh.elizabeth.fedihome.api.iceshrimp.model

import kotlinx.serialization.Serializable

@Serializable
data class SelectUserRequest(
	val userId: String,
)
