package sh.elizabeth.fedihome.api.iceshrimp.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
	val text: String?,
	val fileIds: List<String>? = null,
	val poll: Poll? = null,
	val cw: String? = null,
	val visibility: PostVisibility = PostVisibility.PUBLIC,
	val visibleUserIds: List<String> = emptyList(),
	val localOnly: Boolean = false,
	val noExtractMentions: Boolean = false,
	val noExtractHashtags: Boolean = false,
	val noExtractEmojis: Boolean = false,
	val replyId: String? = null,
	val renoteId: String? = null,
	val channelId: String? = null,
)

@Serializable
data class CreatePostResponse(
	val createdNote: Post,
)
