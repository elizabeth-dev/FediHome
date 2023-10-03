package sh.elizabeth.wastodon.api.mastodon.model

import kotlinx.serialization.Serializable

@Serializable
data class PollVoteRequest(
	val choices: List<Int>,
)
