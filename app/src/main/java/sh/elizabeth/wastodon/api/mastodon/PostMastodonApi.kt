package sh.elizabeth.wastodon.api.mastodon

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.wastodon.api.mastodon.model.CreatePostRequest
import sh.elizabeth.wastodon.api.mastodon.model.PollVoteRequest
import sh.elizabeth.wastodon.api.mastodon.model.Post
import sh.elizabeth.wastodon.api.mastodon.model.PostVisibility
import sh.elizabeth.wastodon.model.PostDraft
import javax.inject.Inject
import sh.elizabeth.wastodon.model.PostVisibility as DomainPostVisibility

class PostMastodonApi @Inject constructor(private val httpClient: HttpClient) {
	// TODO: Use idempotency key
	suspend fun createPost(instance: String, token: String, newPost: PostDraft): Post =
		httpClient.post("https://$instance/api/v1/statuses") {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(newPost.toCreatePost())
		}.body()

	suspend fun fetchPost(instance: String, token: String, postId: String): Post =
		httpClient.get("https://$instance/api/v1/statuses/$postId") {
			bearerAuth(token)
		}.body()

	suspend fun fetchPostsByProfile(
		instance: String,
		token: String,
		profileId: String,
	): List<Post> = httpClient.get("https://$instance/api/v1/accounts/$profileId/statuses") {
		bearerAuth(token)
	}.body()

	suspend fun votePoll(instance: String, token: String, pollId: String, choices: List<Int>) {
		httpClient.post(
			"https://$instance/api/v1/polls/$pollId/votes"
		) {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(PollVoteRequest(choices = choices))
		}
	}
}

// TODO: Implement missing fields
fun PostDraft.toCreatePost() = CreatePostRequest(
	status = text,
	visibility = visibility.toMastodon(),
	cw = cw,
	replyId = replyId,
)

fun DomainPostVisibility.toMastodon(): PostVisibility = when (this) {
	DomainPostVisibility.PUBLIC -> PostVisibility.PUBLIC
	DomainPostVisibility.UNLISTED -> PostVisibility.UNLISTED
	DomainPostVisibility.FOLLOWERS -> PostVisibility.PRIVATE
	DomainPostVisibility.MENTIONED -> PostVisibility.DIRECT
}
