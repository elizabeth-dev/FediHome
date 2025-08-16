package sh.elizabeth.fedihome.api.iceshrimpnet.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
	val id: String,
	val createdAt: String,
	val uri: String,
	val url: String,
	val text: String? = null,
	val cw: String? = null,
	val emojis: List<Emoji> = emptyList(),
	val visibility: PostVisibility = PostVisibility.PUBLIC,
	val localOnly: Boolean = false,
	val bookmarked: Boolean = false,
	val liked: Boolean = false,
	val likes: Int = 0,
	val renotes: Int = 0,
	val replies: Int = 0,
	val user: User,
	val attachments: List<Attachment> = emptyList(),
	val reactions: List<Reaction> = emptyList(),
	val poll: Poll? = null,
	val quote: Post? = null,
	val quoteId: String? = null,
	val quoteInaccessible: Boolean? = null,
	val reply: Post? = null,
	val replyId: String? = null,
	val replyInaccessible: Boolean? = null,
	val renote: Post? = null,
	val renoteId: String? = null,
	val filtered: FilteredSchema? = null,
	val descendants: List<Post>? = emptyList()
)

@Serializable
enum class PostVisibility {
	@SerialName("public")
	PUBLIC,

	@SerialName("home")
	HOME,

	@SerialName("followers")
	FOLLOWERS,

	@SerialName("specified")
	SPECIFIED,
}

@Serializable
data class Reaction(
	val name: String,
	val count: Int,
	val reacted: Boolean,
	val url: String? = null,
	val sensitive: Boolean
)

@Serializable
data class Poll(
	val noteId: String,
	val expiresAt: String? = null,
	val choices: List<PollChoice>,
	val votersCount: Int? = null
)

@Serializable
data class PollChoice(
	val value: String,
	val votes: Int,
	val voted: Boolean,
)

@Serializable
data class FilteredSchema(
	val id: Int,
	val name: String,
	val keyword: String,
	val hide: Boolean,
)