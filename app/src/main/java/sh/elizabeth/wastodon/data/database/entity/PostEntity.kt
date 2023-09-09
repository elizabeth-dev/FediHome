package sh.elizabeth.wastodon.data.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import sh.elizabeth.wastodon.model.Poll
import sh.elizabeth.wastodon.model.PollChoice
import sh.elizabeth.wastodon.model.Post
import java.time.Instant

@Entity(
	foreignKeys = [ForeignKey(
		entity = ProfileEntity::class,
		parentColumns = ["profileId"],
		childColumns = ["authorId"]
	), ForeignKey(
		entity = PostEntity::class,
		parentColumns = ["postId"],
		childColumns = ["quoteId"],
		deferred = true
	)],
	indices = [Index("authorId"), Index(
		value = ["postId"],
		unique = true // FIXME: not actually unique, although calckey api might return a unique id. check this.
	), Index("quoteId")]
)
data class PostEntity(
	@PrimaryKey(autoGenerate = true) var postRow: Long = 0,
	val postId: String,
	val createdAt: Instant?, // Sometimes null on Calckey
	val updatedAt: Instant?,
	val cw: String?,
	val text: String?,
	val authorId: String,
	val quoteId: String?,
	val poll: PollEntity?,
)

data class PollEntity(
	val choices: List<PollChoiceEntity>,
	val expiresAt: Instant?,
	val multiple: Boolean,
)

data class PollChoiceEntity(val text: String, val votes: Int, val isVoted: Boolean)

fun PollEntity.toDomain() = Poll(
	voted = choices.any { it.isVoted },
	multiple = multiple,
	expiresAt = expiresAt,
	choices = choices.map { PollChoice(text = it.text, votes = it.votes, isVoted = it.isVoted) }
)

data class PostWithAuthor(
	@Embedded
	val post: PostEntity,

	@Embedded(prefix = "author_")
	val author: FullProfileEntity,

	@Embedded(prefix = "repostedBy_")
	val repostedBy: FullProfileEntity?,

	@Embedded(prefix = "quotePost_")
	val quotePost: PostEntity?,

	@Embedded(prefix = "quoteAuthor_")
	val quoteAuthor: FullProfileEntity?,
)

fun PostWithAuthor.toPostDomain(): Post = Post(
	id = post.postId,
	createdAt = post.createdAt,
	updatedAt = post.updatedAt,
	cw = post.cw,
	text = post.text,
	author = author.toDomain(),
	repostedBy = repostedBy?.toDomain(),
	quote = if (quotePost != null && quoteAuthor != null) PostWithAuthor(
		// TODO maybe make a specific mapper for this
		post = quotePost,
		author = quoteAuthor,
		repostedBy = null,
		quotePost = null,
		quoteAuthor = null,
	).toPostDomain() else null,
	poll = post.poll?.toDomain(),
)
