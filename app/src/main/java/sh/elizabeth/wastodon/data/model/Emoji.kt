package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Emoji(val name: String, val url: String)
