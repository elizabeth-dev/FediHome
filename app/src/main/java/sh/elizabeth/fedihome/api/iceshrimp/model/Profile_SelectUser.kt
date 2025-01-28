package sh.elizabeth.fedihome.api.iceshrimp.model

import kotlinx.serialization.Serializable

@Serializable
data class SelectUserRequest(
	val userId: String,
)

@Serializable
data class SelectUserByTagRequest(
	val username: String,
	val host: String,
	val limit: Int = 1,
	// Unneeded, missing in Sharkey
//	val maxDaysSinceActive: Int? = null,
	val detail: Boolean = true,
)
