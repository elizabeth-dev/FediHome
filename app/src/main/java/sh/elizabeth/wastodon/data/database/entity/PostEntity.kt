package sh.elizabeth.wastodon.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import sh.elizabeth.wastodon.model.Poll
import sh.elizabeth.wastodon.model.PollChoice
import java.time.Instant

@Entity(
	foreignKeys = [ForeignKey(
		entity = ProfileEntity::class, parentColumns = ["profileId"], childColumns = ["authorId"]
	), ForeignKey(
		entity = PostEntity::class,
		parentColumns = ["postId"],
		childColumns = ["quoteId"],
		deferred = true
	)], indices = [Index("authorId"), Index(
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

fun PollEntity.toDomain() = Poll(voted = choices.any { it.isVoted },
	multiple = multiple,
	expiresAt = expiresAt,
	choices = choices.map { PollChoice(text = it.text, votes = it.votes, isVoted = it.isVoted) })
