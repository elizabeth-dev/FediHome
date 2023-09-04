package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PollVoteRequest(
	val choice: Int,
	val noteId: String,
)
