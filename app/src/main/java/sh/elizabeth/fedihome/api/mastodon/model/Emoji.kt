package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
	val shortcode: String,
	val url: String,
	val static_url: String,
	val visible_in_picker: Boolean,
)

fun Emoji.toDomain(instance: String) =
	sh.elizabeth.fedihome.model.Emoji(
		fullEmojiId = "$shortcode@$instance",
		instance = instance,
		shortcode = shortcode,
		url = url
	)
