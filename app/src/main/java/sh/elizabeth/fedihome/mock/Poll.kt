package sh.elizabeth.fedihome.mock

import sh.elizabeth.fedihome.model.Poll
import sh.elizabeth.fedihome.model.PollChoice

val defaultPoll = Poll(
	id = null,
	voted = false, expiresAt = null, multiple = false, choices = listOf(
		PollChoice(
			text = "foo", votes = 0, isVoted = false
		), PollChoice(
			text = "bar", votes = 0, isVoted = false
		)
	)
)
