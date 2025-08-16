package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.util.InstantAsString

@Serializable
data class FilterResult(
	val filter: Filter,
	@SerialName("keyword_matches") val keywordMatches: List<String>,
	@SerialName("status_matches") val statusMatches: List<String>,
)

@Serializable
data class Filter(
	val id: String,
	val title: String,
	val context: List<FilterContext>,
	@SerialName("expires_at") val expiresAt: InstantAsString? = null,
	@SerialName("filter_action") val filterAction: FilterAction,
	val keywords: List<FilterKeyword>,
	val statuses: FilterStatus,
)

@Serializable
enum class FilterContext {
	@SerialName("home")
	HOME,

	@SerialName("notifications")
	NOTIFICATIONS,

	@SerialName("public")
	PUBLIC,

	@SerialName("thread")
	THREAD,

	@SerialName("account")
	ACCOUNT,
}

@Serializable
enum class FilterAction {
	@SerialName("warn")
	WARN,

	@SerialName("hide")
	HIDE,
}

@Serializable
data class FilterKeyword(
	val id: String,
	val keyword: String,
	@SerialName("whole_word") val wholeWord: Boolean,
)

@Serializable
data class FilterStatus(
	val id: String,
	@SerialName("status_id") val statusId: String,
)
