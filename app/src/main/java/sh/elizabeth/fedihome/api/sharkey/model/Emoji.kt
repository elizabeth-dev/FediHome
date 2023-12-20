package sh.elizabeth.fedihome.api.sharkey.model

import kotlinx.serialization.Serializable

typealias EmojiMap = @Serializable Map<String, String>

fun EmojiMap.toDomainMap(instance: String) = this.mapValues {
	sh.elizabeth.fedihome.model.Emoji(
		fullEmojiId = "${it.key}@$instance", instance = instance, shortcode = it.key, url = it.value
	)
}
