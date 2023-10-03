package sh.elizabeth.fedihome.api.firefish

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.fedihome.api.firefish.model.CreatePostRequest
import sh.elizabeth.fedihome.api.firefish.model.CreatePostResponse
import sh.elizabeth.fedihome.api.firefish.model.GetPostsByProfile
import sh.elizabeth.fedihome.api.firefish.model.PollVoteRequest
import sh.elizabeth.fedihome.api.firefish.model.Post
import sh.elizabeth.fedihome.api.firefish.model.PostVisibility
import sh.elizabeth.fedihome.api.firefish.model.SelectPostRequest
import sh.elizabeth.fedihome.model.PostDraft
import javax.inject.Inject
import sh.elizabeth.fedihome.model.PostVisibility as DomainPostVisibility

class PostFirefishApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun createPost(
		instance: String,
		token: String,
		newPost: PostDraft,
	): CreatePostResponse =
		httpClient.post("https://$instance/api/notes/create") {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(newPost.toCreatePost())
		}.body()

	suspend fun fetchPost(instance: String, token: String, postId: String): Post =
		httpClient.post("https://$instance/api/notes/show") {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(SelectPostRequest(noteId = postId.split('@').first()))
		}.body()

	suspend fun fetchPostsByProfile(
		instance: String,
		token: String,
		profileId: String,
	): List<Post> =
		httpClient.post("https://$instance/api/users/notes") {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(GetPostsByProfile(userId = profileId.split('@').first()))
		}.body()

	suspend fun votePoll(instance: String, token: String, postId: String, choice: Int) {
		httpClient.post(
			"https://$instance/api/notes/polls/vote"
		) {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(PollVoteRequest(noteId = postId.split('@').first(), choice = choice))
		}
	}
}

fun PostDraft.toCreatePost() = CreatePostRequest(
	text = text,
	cw = cw,
	visibility = visibility.toFirefish(),
	visibleUserIds = visibleUserIds,
	localOnly = localOnly,
	replyId = replyId,
	renoteId = renoteId,
	channelId = channelId,
)

fun DomainPostVisibility.toFirefish(): PostVisibility = when (this) {
	DomainPostVisibility.PUBLIC -> PostVisibility.PUBLIC
	DomainPostVisibility.UNLISTED -> PostVisibility.HOME
	DomainPostVisibility.FOLLOWERS -> PostVisibility.FOLLOWERS
	DomainPostVisibility.MENTIONED -> PostVisibility.SPECIFIED
}
