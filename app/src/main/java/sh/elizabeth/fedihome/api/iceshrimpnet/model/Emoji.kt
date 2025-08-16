package sh.elizabeth.fedihome.api.iceshrimpnet.model

import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
	val id: String,
	val name: String,
	val uri: String? = null,
	val tags: List<String> = emptyList(),
	val category: String? = null,
	val publicUrl: String,
	val license: String? = null,
	val sensitive: Boolean,
)