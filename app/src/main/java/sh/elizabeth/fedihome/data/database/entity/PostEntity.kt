package sh.elizabeth.fedihome.data.database.entity

import sh.elizabeth.fedihome.model.Poll
import sh.elizabeth.fedihome.model.PollChoice
import java.time.Instant

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

