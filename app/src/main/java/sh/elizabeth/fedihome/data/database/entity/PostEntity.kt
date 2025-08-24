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

data class AttachmentEntity(
	val id: String,
	val description: String?,
	val type: AttachmentEntityType,
	val url: String,
	val blurhash: String?
)

enum class AttachmentEntityType {
	IMAGE, VIDEO, AUDIO, UNKNOWN,
}

fun AttachmentEntity.toDomain() = sh.elizabeth.fedihome.model.Attachment(
	id = id,
	description = description,
	type = when (type) {
		AttachmentEntityType.IMAGE -> sh.elizabeth.fedihome.model.AttachmentType.IMAGE
		AttachmentEntityType.VIDEO -> sh.elizabeth.fedihome.model.AttachmentType.VIDEO
		AttachmentEntityType.AUDIO -> sh.elizabeth.fedihome.model.AttachmentType.AUDIO
		AttachmentEntityType.UNKNOWN -> sh.elizabeth.fedihome.model.AttachmentType.UNKNOWN
	},
	url = url,
	blurhash = blurhash,
)