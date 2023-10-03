package sh.elizabeth.fedihome.model

data class PostDraft(
	val text: String?,
	val cw: String?,
	val visibility: PostVisibility,
	val visibleUserIds: List<String>,
	val localOnly: Boolean,
	/* val noExtractMentions: Boolean = false,
	val noExtractHashtags: Boolean = false,
	val noExtractEmojis: Boolean = false, */
	val replyId: String?,
	val renoteId: String?,
	val channelId: String?,
)
