package sh.elizabeth.wastodon.model

import java.time.Instant

data class Post(
	val id: String,
	val createdAt: Instant?, // Sometimes null on Calckey
	val updatedAt: Instant?,
	val cw: String?,
	val text: String?,
	val author: Profile,
	val repostedBy: Profile?,
	val quote: Post?,
)

fun Post.unwrapQuotes(): List<Post> {
	val quotes = mutableListOf(this)
	var currentQuote = quote
	while (currentQuote != null) {
		quotes.add(currentQuote)
		currentQuote = currentQuote.quote
	}
	return quotes
}

fun Post.unwrapProfiles(): List<Profile> =
	if (repostedBy == null) listOf(author) else listOf(author, repostedBy)
