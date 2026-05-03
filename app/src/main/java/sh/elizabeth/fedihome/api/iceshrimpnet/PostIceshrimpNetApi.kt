package sh.elizabeth.fedihome.api.iceshrimpnet

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import sh.elizabeth.fedihome.api.mastodon.model.Post
import javax.inject.Inject

class PostIceshrimpNetApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun createReaction(
		endpoint: String,
		token: String,
		postId: String,
		reaction: String
	): Post = httpClient.post(
		"https://$endpoint/api/v1/statuses/$postId/react/$reaction"
	) {
		bearerAuth(token)
	}.body()

	suspend fun removeReaction(
		endpoint: String,
		token: String,
		postId: String,
		reaction: String
	): Post = httpClient.post(
		"https://$endpoint/api/v1/statuses/$postId/unreact/$reaction"
	) {
		bearerAuth(token)
	}.body()
}