package sh.elizabeth.fedihome.api.iceshrimpnet.model

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
	val url: String,
	val thumbnailUrl: String? = null,
	val contentType: String,
	val isSensitive: Boolean = false,
	val blurhash: String? = null,
	val altText: String? = null,
	val fileName: String? = null
)
