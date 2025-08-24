package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.model.Attachment
import sh.elizabeth.fedihome.model.AttachmentType

@Serializable
data class Media(
	val id: String,
	val type: MediaType,
	val url: String,
	val preview_url: String,
	val remote_url: String? = null,
	val _meta: String?, // FIXME: object
	val description: String? = null,
	val blurhash: String? = null,
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

fun Media.toDomain() = Attachment(
	id = id,
	description = description,
	type = when (type) {
		MediaType.IMAGE, MediaType.GIF -> AttachmentType.IMAGE
		MediaType.VIDEO -> AttachmentType.VIDEO
		MediaType.AUDIO -> AttachmentType.AUDIO
		else -> AttachmentType.UNKNOWN
	},
	url = url,
	blurhash = blurhash
)
