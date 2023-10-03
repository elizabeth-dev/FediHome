package sh.elizabeth.wastodon.api.mastodon.model

import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
	val shortcode: String,
	val url: String,
	val static_url: String,
	val visible_in_picker: Boolean,
)

fun Emoji.toDomain(instance: String) =
	sh.elizabeth.wastodon.model.Emoji(
		fullEmojiId = "$shortcode@$instance",
		instance = instance,
		shortcode = shortcode,
		url = url
	)
