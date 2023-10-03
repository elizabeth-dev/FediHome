package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Media(
	val id: String,
	val type: MediaType,
	val url: String,
	val preview_url: String,
	val remote_url: String? = null,
	val _meta: String?, // FIXME: object
	val description: String? = null,
	val blurhash: String,
)

@Serializable
enum class MediaType {
	@SerialName("image")
	IMAGE,

	@SerialName("gifv")
	GIF,

	@SerialName("video")
	VIDEO,

	@SerialName("audio")
	AUDIO,

	@SerialName("hidden") // ???
	HIDDEN,
}
