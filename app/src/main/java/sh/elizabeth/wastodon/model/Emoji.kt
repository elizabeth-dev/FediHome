package sh.elizabeth.wastodon.model

data class Emoji(
	val fullEmojiId: String,
	val instance: String,
	val shortcode: String,
	val url: String,
) {
	override fun equals(other: Any?): Boolean = fullEmojiId == (other as? Emoji)?.fullEmojiId

	override fun hashCode(): Int = fullEmojiId.hashCode()
}
