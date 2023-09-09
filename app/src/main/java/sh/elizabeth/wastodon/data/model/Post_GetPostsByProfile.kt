package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GetPostsByProfile(
	val userId: String,
	val limit: Int? = 10,
	val sinceId: String? = null,
	val untilId: String? = null,
	val sinceDate: Long? = null,
	val untilDate: Long? = null,
	val includeMyRenotes: Boolean? = true,
	val includeReplies: Boolean? = true,
	val withFiles: Boolean? = false,
	val fileType: List<String>? = null,
	val excludeNsfw: Boolean = false,
)
