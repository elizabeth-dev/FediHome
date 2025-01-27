package sh.elizabeth.fedihome.api.iceshrimp.model

import kotlinx.serialization.Serializable

@Serializable
data class GetNotificationsRequest(
	val limit: Int? = null,
	val sinceId: String? = null,
	val untilId: String? = null,
	val following: Boolean? = null,
	val unreadOnly: Boolean? = null,
	val directOnly: Boolean? = null,
	val markAsRead: Boolean? = null,
	val includeTypes: List<String>? = null,
	val excludeTypes: List<String>? = null,
)
