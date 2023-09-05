package sh.elizabeth.wastodon.model

data class Profile(
	val id: String,
	val name: String?,
	val username: String,
	val instance: String,
	val fullUsername: String,
	val avatarUrl: String?,
	val avatarBlur: String?,
	val headerUrl: String?,
)
