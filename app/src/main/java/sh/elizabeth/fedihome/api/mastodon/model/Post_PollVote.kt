package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.Serializable

@Serializable
data class PollVoteRequest(
	val choices: List<Int>,
)
