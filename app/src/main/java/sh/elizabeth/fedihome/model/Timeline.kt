package sh.elizabeth.fedihome.model

data class TimelinePostItem(
	val post: Post,
	val forAccount: String,
	val type: TimelinePostItemType,
)

enum class TimelinePostItemType {
	POST,
	PLACEHOLDER
}
