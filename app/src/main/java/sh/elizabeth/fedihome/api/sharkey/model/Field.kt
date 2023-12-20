package sh.elizabeth.fedihome.api.sharkey.model

import kotlinx.serialization.Serializable

@Serializable
data class Field(val name: String, val value: String, val verified: Boolean = false)
