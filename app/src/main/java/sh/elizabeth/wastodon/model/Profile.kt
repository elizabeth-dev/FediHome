package sh.elizabeth.wastodon.model

import java.time.Instant

data class Profile(
	val id: String,
	val username: String,
	val instance: String,
	val fullUsername: String,
	val name: String?,
	val description: String?,
	val following: Int?,
	val followers: Int?,
	val postCount: Int?,
	val createdAt: Instant?,
	val fields: List<ProfileField>,
	val avatarUrl: String?,
	val avatarBlur: String?,
	val headerUrl: String?,
	val headerBlur: String?,
	val emojis: Map<String, Emoji>,
)

data class ProfileField(
	val name: String,
	val value: String,
)
