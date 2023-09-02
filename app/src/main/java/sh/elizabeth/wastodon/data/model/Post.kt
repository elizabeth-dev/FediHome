package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.Serializable
import sh.elizabeth.wastodon.util.InstantAsString
import sh.elizabeth.wastodon.model.Post as DomainPost

@Serializable
data class Post(
	val id: String,
	val createdAt: InstantAsString,
	val updatedAt: InstantAsString? = null,
	val text: String? = null,
	val cw: String? = null,
	val userId: String,
	val user: UserLite,
	val replyId: String? = null,
	val reply: Post? = null,
	val renoteId: String? = null,
	val renote: Post? = null,
	val visibility: String,
	val mentions: List<String>? = emptyList(), // Strings are user ids
	// val visibleUserIds: List<String>, // Never seen in blahaj.zone API
	val fileIds: List<String>,
	val files: List<File>,
	val tags: List<String>? = null,
	val poll: Poll? = null,
//	val channelId: String? = null, // Never seen in blahaj.zone API
//	val channel: Channel? = null, // Never seen in blahaj.zone API
	val localOnly: Boolean? = false,
	val emojis: List<Emoji>,
	val reactions: Map<String, Int>,
	val reactionEmojis: List<Emoji>,
	val renoteCount: Int,
	val repliesCount: Int,
	val uri: String? = null,
	val url: String? = null,
	val myReaction: String? = null,
)

@Serializable
data class Poll(
	val multiple: Boolean,
	val expiresAt: InstantAsString? = null,
	val choices: List<PollChoice>,
)

@Serializable
data class PollChoice(
	val text: String,
	val votesCount: Int,
	val isVoted: Boolean,
)

fun Post.toDomain(fetchedFromInstance: String) = DomainPost(
	id = id,
	createdAt = createdAt,
	updatedAt = updatedAt,
	text = text ?: "boost",
	cw = cw,
	author = user.toDomain(fetchedFromInstance),
)
