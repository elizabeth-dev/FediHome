package sh.elizabeth.fedihome.api.sharkey.model

import kotlinx.serialization.Serializable

@Serializable
data class GetNotificationsRequest(
	val limit: Int? = null, val sinceId: String? = null, val untilId: String? = null, val markAsRead: Boolean? = null,
	val includeTypes: List<String>? = null, val excludeTypes: List<String>? = null,
)
