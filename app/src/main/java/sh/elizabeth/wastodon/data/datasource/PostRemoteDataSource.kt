package sh.elizabeth.wastodon.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import sh.elizabeth.wastodon.data.model.CreatePostRequest
import sh.elizabeth.wastodon.data.model.CreatePostResponse
import sh.elizabeth.wastodon.data.model.GetPostsByProfile
import sh.elizabeth.wastodon.data.model.PollVoteRequest
import sh.elizabeth.wastodon.data.model.Post
import sh.elizabeth.wastodon.data.model.SelectPostRequest
import sh.elizabeth.wastodon.model.PostDraft
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(private val httpClient: HttpClient) {
	suspend fun createPost(instance: String, newPost: PostDraft): CreatePostResponse =
		httpClient.post("https://$instance/api/notes/create") {
			contentType(ContentType.Application.Json)
			setBody(newPost.toCreatePost())
		}.body()

	suspend fun fetchPost(instance: String, postId: String): Post =
		httpClient.post("https://$instance/api/notes/show") {
			contentType(ContentType.Application.Json)
			setBody(SelectPostRequest(noteId = postId))
		}.body()

	suspend fun fetchPostsByProfile(instance: String, profile: String): List<Post> =
		httpClient.post("https://$instance/api/users/notes") {
			contentType(ContentType.Application.Json)
			setBody(GetPostsByProfile(userId = profile))
		}.body()

	suspend fun votePoll(instance: String, postId: String, choice: Int) {
		httpClient.post(
			"https://$instance/api/notes/polls/vote"
		) {
			contentType(ContentType.Application.Json)
			setBody(PollVoteRequest(noteId = postId, choice = choice))
		}
	}
}

fun PostDraft.toCreatePost() = CreatePostRequest(
	text = text,
	cw = cw,
	visibility = visibility,
	visibleUserIds = visibleUserIds,
	localOnly = localOnly,
	replyId = replyId,
	renoteId = renoteId,
	channelId = channelId,
)
