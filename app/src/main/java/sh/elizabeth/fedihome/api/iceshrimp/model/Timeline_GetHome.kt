package sh.elizabeth.fedihome.api.iceshrimp.model

import kotlinx.serialization.Serializable

@Serializable
data class GetHomeRequest(
	val limit: Int? = 10,
	val sinceId: String? = null,
	val untilId: String? = null,
	val sinceDate: Long? = null,
	val untilDate: Long? = null,
	val includeMyRenotes: Boolean? = true,
	val includeRenotedMyNotes: Boolean? = true,
	val includeLocalRenotes: Boolean? = true,
	val withFiles: Boolean? = false,
)
