package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Field(val name: String, val value: String)
