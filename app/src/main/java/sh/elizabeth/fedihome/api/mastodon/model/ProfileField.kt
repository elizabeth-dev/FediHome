package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.util.InstantAsString

@Serializable
data class ProfileField(
	val name: String,
	val value: String, // Needs to be cleaned up
	val verifiedAt: InstantAsString? = null,
)
