package sh.elizabeth.wastodon.data.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import sh.elizabeth.wastodon.model.Post

class EnrichedPost(
	@Embedded val post: PostEntity,

	@Relation(
		parentColumn = "postId",
		entityColumn = "fullEmojiId",
		associateBy = Junction(PostEmojiCrossRef::class)
	) val postEmojis: List<EmojiEntity>,

	@Embedded(prefix = "author_") val author: FullProfile,

	@Relation(
		parentColumn = "author_profileId",
		entityColumn = "fullEmojiId",
		associateBy = Junction(ProfileEmojiCrossRef::class, "profileId", "fullEmojiId")
	) val authorEmojis: List<EmojiEntity>,

	@Embedded(prefix = "quotePost_") val quotePost: PostEntity?,

	@Relation(
		parentColumn = "quotePost_postId",
		entityColumn = "fullEmojiId",
		associateBy = Junction(PostEmojiCrossRef::class, "postId", "fullEmojiId")
	) val quotePostEmojis: List<EmojiEntity>,

	@Embedded(prefix = "quoteAuthor_") val quoteAuthor: FullProfile?,

	@Relation(
		parentColumn = "quoteAuthor_profileId",
		entityColumn = "fullEmojiId",
		associateBy = Junction(ProfileEmojiCrossRef::class, "profileId", "fullEmojiId")
	) val quoteAuthorEmojis: List<EmojiEntity>,
)

fun EnrichedPost.toPostDomain(): Post = EnrichedTimelinePost(
	post = post,
	postEmojis = postEmojis,
	author = author,
	authorEmojis = authorEmojis,
	repostedBy = null,
	repostedByEmojis = emptyList(),
	quotePost = quotePost,
	quotePostEmojis = quotePostEmojis,
	quoteAuthor = quoteAuthor,
	quoteAuthorEmojis = quoteAuthorEmojis,
).toPostDomain()

class EnrichedTimelinePost(
	@Embedded val post: PostEntity,

	@Relation(
		parentColumn = "postId",
		entityColumn = "fullEmojiId",
		associateBy = Junction(PostEmojiCrossRef::class)
	) val postEmojis: List<EmojiEntity>,

	@Embedded(prefix = "author_") val author: FullProfile,

	@Relation(
		parentColumn = "author_profileId",
		entityColumn = "fullEmojiId",
		associateBy = Junction(ProfileEmojiCrossRef::class, "profileId", "fullEmojiId")
	) val authorEmojis: List<EmojiEntity>,

	@Embedded(prefix = "repostedBy_") val repostedBy: FullProfile?,

	@Relation(
		parentColumn = "repostedBy_profileId",
		entityColumn = "fullEmojiId",
		associateBy = Junction(ProfileEmojiCrossRef::class, "profileId", "fullEmojiId")
	) val repostedByEmojis: List<EmojiEntity>,

	@Embedded(prefix = "quotePost_") val quotePost: PostEntity?,

	@Relation(
		parentColumn = "quotePost_postId",
		entityColumn = "fullEmojiId",
		associateBy = Junction(PostEmojiCrossRef::class, "postId", "fullEmojiId")
	) val quotePostEmojis: List<EmojiEntity>,

	@Embedded(prefix = "quoteAuthor_") val quoteAuthor: FullProfile?,

	@Relation(
		parentColumn = "quoteAuthor_profileId",
		entityColumn = "fullEmojiId",
		associateBy = Junction(ProfileEmojiCrossRef::class, "profileId", "fullEmojiId")
	) val quoteAuthorEmojis: List<EmojiEntity>,
)

fun EnrichedTimelinePost.toPostDomain(): Post = Post(id = post.postId,
	createdAt = post.createdAt,
	updatedAt = post.updatedAt,
	cw = post.cw,
	text = post.text,
	author = author.toDomain(authorEmojis),
	repostedBy = repostedBy?.toDomain(repostedByEmojis),
	quote = if (quotePost != null && quoteAuthor != null) EnrichedPost(
		// TODO maybe make a specific mapper for this
		post = quotePost,
		postEmojis = quotePostEmojis,
		author = quoteAuthor,
		authorEmojis = quoteAuthorEmojis,
		quotePost = null,
		quotePostEmojis = emptyList(),
		quoteAuthor = null,
		quoteAuthorEmojis = emptyList(),
	).toPostDomain() else null,
	poll = post.poll?.toDomain(),
	emojis = postEmojis.associate { Pair(it.shortcode, it.toDomain()) })
