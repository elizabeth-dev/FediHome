package sh.elizabeth.wastodon.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.wastodon.util.InstantAsString

@Serializable
data class FilterResult(
	val filter: Filter,
	val keyword_matches: List<String>? = null,
	val status_matches: String? = null,
)

@Serializable
data class Filter(
	val id: String,
	val title: String,
	val context: List<FilterContext>,
	val expires_at: InstantAsString? = null,
	val filter_action: FilterAction,
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
	val whole_word: Boolean,
)

@Serializable
data class FilterStatus(
	val id: String,
	val status_id: String,
)
