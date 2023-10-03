package sh.elizabeth.fedihome.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import sh.elizabeth.fedihome.model.Poll
import sh.elizabeth.fedihome.model.PollChoice
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
		value = ["postId"], unique = true
	), Index("quoteId")]
)
data class PostEntity(
	@PrimaryKey val postId: String,
	val createdAt: Instant?, // Sometimes null on Calckey
	val updatedAt: Instant?,
	val cw: String?,
	val text: String?,
	val authorId: String,
	val quoteId: String?,
	val poll: PollEntity?,
)

data class PollEntity(
	val id: String?,
	val choices: List<PollChoiceEntity>,
	val expiresAt: Instant?,
	val multiple: Boolean,
)

data class PollChoiceEntity(val text: String, val votes: Int?, val isVoted: Boolean)

fun PollEntity.toDomain() = Poll(
	id = id,
	voted = choices.any { it.isVoted },
	multiple = multiple,
	expiresAt = expiresAt,
	choices = choices.map { PollChoice(text = it.text, votes = it.votes, isVoted = it.isVoted) })
