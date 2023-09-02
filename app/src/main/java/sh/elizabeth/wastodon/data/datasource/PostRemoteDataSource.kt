package sh.elizabeth.wastodon.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.contentType
import sh.elizabeth.wastodon.data.model.CreatePostRequest
import sh.elizabeth.wastodon.data.model.CreatePostResponse
import sh.elizabeth.wastodon.model.PostDraft
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(private val httpClient: HttpClient) {
	suspend fun createPost(instance: String, newPost: PostDraft): CreatePostResponse =
		httpClient.post("https://$instance/api/notes/create") {
			contentType(io.ktor.http.ContentType.Application.Json)
			setBody(newPost.toCreatePost())
		}.body()
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
