package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateSessionRequest(val appSecret: String)

@Serializable
data class GenerateSessionResponse(
    val token: String,
    val url: String,
)
