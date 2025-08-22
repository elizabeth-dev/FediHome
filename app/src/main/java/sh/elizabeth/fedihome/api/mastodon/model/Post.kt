package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.model.PollChoice
import sh.elizabeth.fedihome.util.InstantAsString
import sh.elizabeth.fedihome.model.Poll as DomainPoll
import sh.elizabeth.fedihome.model.Post as DomainPost

@Serializable
data class Post(
	val id: String,
	val uri: String,
	@SerialName("created_at") val createdAt: InstantAsString,
	@SerialName("edited_at") val editedAt: InstantAsString? = null,
	val account: Profile,
	val content: String,
	val visibility: PostVisibility,
	val sensitive: Boolean,
	@SerialName("spoiler_text") val spoilerText: String,
	@SerialName("media_attachments") val mediaAttachments: List<Media>,
	val application: Application? = null,
	val mentions: List<Mention>,
	val tags: List<Hashtag>,
	val emojis: List<Emoji>,
	@SerialName("reblogs_count") val reblogsCount: Long,
	@SerialName("favourites_count") val favouritesCount: Long,
	@SerialName("replies_count") val repliesCount: Long,
	val url: String? = null,
	@SerialName("in_reply_to_id") val inReplyToId: String? = null,
	@SerialName("in_reply_to_account_id") val inReplyToAccountId: String? = null,
	val reblog: Post? = null,
	val poll: Poll? = null,
	val card: Card? = null,
	val language: String? = null,
	@SerialName("text") val text_onlyWhen_Deleted: String? = null,
	val favourited: Boolean,
	val reblogged: Boolean,
	val muted: Boolean,
	val bookmarked: Boolean,
	val pinned: Boolean? = null,
	val filtered: List<FilterResult>,
	// From Iceshrimp.NET
	val reactions: List<Reaction>? = null,
	val quote: Post? = null,
	@SerialName("quote_id") val quoteId: String? = null,
)

fun Post.toDomain(fetchedFromInstance: String): DomainPost {
	if (reblog != null) {
		return DomainPost(
			id = "${reblog.id}@$fetchedFromInstance",
			createdAt = reblog.createdAt,
			updatedAt = reblog.editedAt,
			text = reblog.content,
			cw = reblog.spoilerText,
			author = reblog.account.toDomain(fetchedFromInstance),
			repostedBy = account.toDomain(fetchedFromInstance),
			quote = null,
			poll = reblog.poll?.toDomain(),
			reactions = reblog.reactions?.associate {
				Pair(
					it.name, it.count
				)
			} ?: emptyMap(),
			myReaction = reblog.reactions?.firstOrNull { it.me }?.name,
			emojis = reblog.emojis.associate {
				Pair(
					it.shortcode, it.toDomain(fetchedFromInstance)
				)
			}
				.plus(reblog.reactions?.filter { !it.url.isNullOrBlank() || !it.staticUrl.isNullOrBlank() }
					?.associate {
						Pair(
							it.name, it.toEmojiDomain(fetchedFromInstance)
						)
					} ?: emptyMap()),
			favorites = reblog.favouritesCount,
			favorited = reblog.favourited,
			mentionLinksMap = reblog.mentions.associate {
				Pair(
					it.url,
					if (it.acct.contains('@')) it.acct else "${it.acct}@$fetchedFromInstance"
				)
			})
	}

	return DomainPost(
		id = "${id}@$fetchedFromInstance",
		createdAt = createdAt,
		updatedAt = editedAt,
		text = content,
		cw = spoilerText,
		author = account.toDomain(fetchedFromInstance),
		repostedBy = null,
		quote = null,
		poll = poll?.toDomain(),
		reactions = reactions?.associate {
			Pair(
				it.name, it.count
			)
		} ?: emptyMap(),
		myReaction = reactions?.firstOrNull { it.me }?.name,
		emojis = emojis.associate {
			Pair(
				it.shortcode,
				it.toDomain(fetchedFromInstance) // TODO: should this be shortcode? what if two instances use the same shortcode?
			)
		}.plus(reactions?.filter { !it.url.isNullOrBlank() || !it.staticUrl.isNullOrBlank() }
			?.associate {
				Pair(
					it.name, it.toEmojiDomain(fetchedFromInstance)
				)
			} ?: emptyMap()),
		favorites = favouritesCount,
		favorited = favourited,
		mentionLinksMap = mentions.associate {
			Pair(
				it.url,
				if (it.acct.contains('@')) it.acct else "${it.acct}@$fetchedFromInstance"
			)
		})
}

@Serializable
data class Reaction(
	val name: String,
	val count: Int,
	val me: Boolean,
	val url: String? = null,
	@SerialName("static_url") val staticUrl: String? = null,
	val accounts: List<Profile>? = null,
	@SerialName("account_ids") val accountIds: List<String>
)

fun Reaction.toEmojiDomain(fetchedFromInstance: String) = sh.elizabeth.fedihome.model.Emoji(
	shortcode = name.split('@').first(),
	url = url ?: staticUrl!!,
	instance = if (name.contains('@')) name.split('@').last() else fetchedFromInstance,
	fullEmojiId = if (name.contains('@')) name else "$name@$fetchedFromInstance",
)

@Serializable
enum class PostVisibility {
	@SerialName("public")
	PUBLIC,

	@SerialName("unlisted")
	UNLISTED,

	@SerialName("private")
	PRIVATE,

	@SerialName("direct")
	DIRECT,
}

@Serializable
data class Application(
	val name: String,
	val website: String? = null,
)

@Serializable
data class Mention(
	val id: String,
	val url: String,
	val username: String,
	val acct: String,
)

@Serializable
data class Hashtag(
	val name: String,
	val url: String,
)

@Serializable
data class Poll(
	val id: String,
	@SerialName("expires_at") val expiresAt: InstantAsString?,
	val expired: Boolean,
	val multiple: Boolean,
	val votes_count: Int,
	val voters_count: Int?,
	val options: List<PollOption>,
	val emojis: List<Emoji>,
	val voted: Boolean,
	@SerialName("own_votes") val ownVotes: List<Int>,
)

fun Poll.toDomain() = DomainPoll(
	id = id,
	voted = voted,
	multiple = multiple,
	expiresAt = expiresAt,
	choices = options.mapIndexed { i, it ->
		PollChoice(
			text = it.title, votes = it.votesCount, isVoted = ownVotes.contains(i)
		)
	})

@Serializable
data class PollOption(
	val title: String,
	@SerialName("votes_count") val votesCount: Int?,
)

@Serializable
data class Card(
	val url: String,
	val title: String,
	val description: String,
	val type: CardType,
	val author_name: String,
	val author_url: String,
	val provider_name: String,
	val provider_url: String,
	val html: String,
	val width: Int,
	val height: Int,
	val image: String?,
	val embed_url: String,
	val blurhash: String?,
	val filtered: List<FilterResult>? = null,
)

@Serializable
enum class CardType {
	@SerialName("link")
	LINK,

	@SerialName("photo")
	PHOTO,

	@SerialName("video")
	VIDEO,

	@SerialName("rich")
	RICH,
}
