package sh.elizabeth.wastodon.api.firefish.model

import kotlinx.serialization.Serializable

@Serializable
data class PollVoteRequest(
	val choice: Int,
	val noteId: String,
)
