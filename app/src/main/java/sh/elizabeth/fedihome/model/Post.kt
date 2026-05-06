package sh.elizabeth.fedihome.model

import java.time.Instant

data class Post(
	val id: String,
	val createdAt: Instant?, // Sometimes null on Calckey
	val updatedAt: Instant?,
	val cw: String?,
	val text: String?,
	val author: Profile,
	val boostedBy: Profile?,
	val boosted: Boolean,
	val boosts: Long,
	val boostedPost: Post? = null,
	val quote: Post?,
	val poll: Poll?,
	val reactions: Map<String, Int>,
	val myReactions: List<String>,
	val favorites: Long,
	val favorited: Boolean,
	val emojis: Map<String, Emoji>,
	val mentionLinksMap: Map<String, String>? = null,
	val attachments: List<Attachment>,
)

fun Post.unwrapPosts(): List<Post> {
	return _unwrapPosts().distinctBy(Post::id).reversed()
}

private fun Post._unwrapPosts(): List<Post> {
	val posts = mutableListOf(this)

	if (quote != null) posts.addAll(quote._unwrapPosts())
	if (boostedPost != null) posts.addAll(boostedPost._unwrapPosts())

	return posts
}

fun Post.unwrapProfiles(): List<Profile> = listOfNotNull(author, boostedBy).distinctBy(Profile::id)

data class Poll(
	val id: String?,
	val voted: Boolean,
	val choices: List<PollChoice>,
	val expiresAt: Instant?,
	val multiple: Boolean,
)

data class PollChoice(val text: String, val votes: Int?, val isVoted: Boolean)

data class Attachment(
	val id: String,
	val description: String?,
	val type: AttachmentType,
	val url: String,
	val blurhash: String?,
)

enum class AttachmentType {
	IMAGE, VIDEO, AUDIO, UNKNOWN,
}