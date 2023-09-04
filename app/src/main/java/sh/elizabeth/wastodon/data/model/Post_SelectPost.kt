package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SelectPostRequest(val noteId: String)
