package sh.elizabeth.wastodon.model

import java.time.Instant

data class Post(
	val id: String,
	val createdAt: Instant?, // Sometimes null on Calckey
	val updatedAt: Instant?,
	val cw: String?,
	val text: String,
	val author: Profile,
)
