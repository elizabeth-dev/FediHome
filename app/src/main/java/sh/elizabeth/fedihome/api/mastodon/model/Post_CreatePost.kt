package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.util.InstantAsString

@Serializable
data class CreatePostRequest(
	val status: String?,
	@SerialName("spoiler_text") val cw: String? = null,
	@SerialName("in_reply_to_id") val replyId: String? = null,
	val visibility: PostVisibility = PostVisibility.PUBLIC,
	val sensitive: Boolean? = false,
	@SerialName("media_ids") val mediaIds: List<String>? = emptyList(),
	@SerialName("scheduled_at") val scheduledAt: InstantAsString? = null,
	val poll: NewPoll? = null,
	val language: String? = null,
)

@Serializable
data class NewPoll(
	val options: List<String>,
	@SerialName("expires_in") val expiresIn: Long,
	val multiple: Boolean?,
	@SerialName("hide_totals") val hideResults: Boolean?,
)
