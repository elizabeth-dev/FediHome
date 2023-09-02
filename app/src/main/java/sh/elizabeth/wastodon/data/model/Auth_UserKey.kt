package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserKeyRequest(val appSecret: String, val token: String)

@Serializable
data class UserKeyResponse(val accessToken: String, val user: UserDetailedNotMe)
