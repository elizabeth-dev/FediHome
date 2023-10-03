package sh.elizabeth.wastodon.api.firefish.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.wastodon.util.InstantAsString
import sh.elizabeth.wastodon.model.Poll as DomainPoll
import sh.elizabeth.wastodon.model.PollChoice as DomainPollChoice
import sh.elizabeth.wastodon.model.Post as DomainPost

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

	@SerialName("hidden") // ???
	HIDDEN,
}

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
	val visibility: PostVisibility,
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

// Needs to have text, poll, files, or maybe a reply?
fun Post.isQuote(): Boolean =
	renote != null && (!text.isNullOrBlank() || poll != null || files.isNotEmpty() || reply != null)

fun Post.toDomain(fetchedFromInstance: String): DomainPost {
	if (renote != null && !isQuote()) {
		return DomainPost(
			id = "${renote.id}@$fetchedFromInstance",
			createdAt = renote.createdAt,
			updatedAt = renote.updatedAt,
			text = renote.text,
			cw = renote.cw,
			author = renote.user.toDomain(fetchedFromInstance),
			repostedBy = user.toDomain(fetchedFromInstance),
			quote = renote.renote?.toDomain(fetchedFromInstance),
			poll = renote.poll?.toDomain(),
			// TODO: Add reaction emojis
			emojis = renote.emojis.associate { Pair(it.name, it.toDomain(fetchedFromInstance)) },
		)
	}
	return DomainPost(
		id = "$id@$fetchedFromInstance",
		createdAt = createdAt,
		updatedAt = updatedAt,
		text = text,
		cw = cw,
		author = user.toDomain(fetchedFromInstance),
		repostedBy = null,
		quote = renote?.toDomain(fetchedFromInstance),
		poll = poll?.toDomain(),
		// TODO: Add reaction emojis
		emojis = emojis.associate { Pair(it.name, it.toDomain(fetchedFromInstance)) },
	)
}

@Serializable
data class Poll(
	val multiple: Boolean,
	val expiresAt: InstantAsString? = null,
	val expiredAfter: Int? = null, // Millis, only present when posting
	val choices: List<PollChoice>,
)

@Serializable
data class PollChoice(
	val text: String,
	val votes: Int,
	val isVoted: Boolean,
)

fun Poll.toDomain() = DomainPoll(
	id = null, voted = choices.any { it.isVoted },
	multiple = multiple,
	expiresAt = expiresAt,
	choices = choices.map {
		DomainPollChoice(
			text = it.text, votes = it.votes, isVoted = it.isVoted
		)
	})
