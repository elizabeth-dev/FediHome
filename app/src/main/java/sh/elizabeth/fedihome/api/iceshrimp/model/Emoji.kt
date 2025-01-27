package sh.elizabeth.fedihome.api.iceshrimp.model

import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.model.Emoji as DomainEmoji

@Serializable
data class Emoji(val name: String, val url: String, val width: Int?, val height: Int?)

fun Emoji.toDomain(instance: String) =
	DomainEmoji(fullEmojiId = "$name@$instance", instance = instance, shortcode = name, url = url)
