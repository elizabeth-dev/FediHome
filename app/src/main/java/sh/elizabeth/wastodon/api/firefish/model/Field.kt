package sh.elizabeth.wastodon.api.firefish.model

import kotlinx.serialization.Serializable

@Serializable
data class Field(val name: String, val value: String)
