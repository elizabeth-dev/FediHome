package sh.elizabeth.fedihome.api.iceshrimp.model

import kotlinx.serialization.Serializable

@Serializable
data class PollVoteRequest(
	val choice: Int,
	val noteId: String,
)
