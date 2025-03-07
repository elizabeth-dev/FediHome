package sh.elizabeth.fedihome.api.sharkey

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.fedihome.api.iceshrimp.model.GetPostsByProfile
import sh.elizabeth.fedihome.api.iceshrimp.model.SelectPostRequest
import sh.elizabeth.fedihome.api.sharkey.model.CreatePostRequest
import sh.elizabeth.fedihome.api.sharkey.model.CreatePostResponse
import sh.elizabeth.fedihome.api.sharkey.model.Post
import sh.elizabeth.fedihome.api.sharkey.model.PostVisibility
import sh.elizabeth.fedihome.model.PostDraft
import javax.inject.Inject
import sh.elizabeth.fedihome.model.PostVisibility as DomainPostVisibility

class PostSharkeyApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun createPost(
		endpoint: String,
		token: String,
		newPost: PostDraft,
	): CreatePostResponse =
		httpClient.post("https://$endpoint/api/notes/create") {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(newPost.toCreatePost())
		}.body()

	suspend fun fetchPost(endpoint: String, token: String, postId: String): Post =
		httpClient.post("https://$endpoint/api/notes/show") {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(SelectPostRequest(noteId = postId.split('@').first()))
		}.body()

	suspend fun fetchPostsByProfile(
		endpoint: String,
		token: String,
		profileId: String,
	): List<Post> =
		httpClient.post("https://$endpoint/api/users/notes") {
			contentType(ContentType.Application.Json)
			bearerAuth(token)
			setBody(GetPostsByProfile(userId = profileId.split('@').first()))
		}.body()
}

fun PostDraft.toCreatePost() = CreatePostRequest(
	text = text,
	cw = cw,
	visibility = visibility.toIceshrimp(),
	visibleUserIds = visibleUserIds,
	localOnly = localOnly,
	replyId = replyId,
	renoteId = renoteId,
	channelId = channelId,
)

fun DomainPostVisibility.toIceshrimp(): PostVisibility = when (this) {
	DomainPostVisibility.PUBLIC -> PostVisibility.PUBLIC
	DomainPostVisibility.UNLISTED -> PostVisibility.HOME
	DomainPostVisibility.FOLLOWERS -> PostVisibility.FOLLOWERS
	DomainPostVisibility.MENTIONED -> PostVisibility.SPECIFIED
}
