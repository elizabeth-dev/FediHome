package sh.elizabeth.fedihome.api.firefish.model

import kotlinx.serialization.Serializable

@Serializable
data class PollVoteRequest(
	val choice: Int,
	val noteId: String,
)
