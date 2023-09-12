package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.Serializable
import sh.elizabeth.wastodon.model.Profile

@Serializable
data class UserLite(
	val id: String,
	val name: String? = null,
	val username: String,
	val host: String? = null,
	val avatarUrl: String? = null,
	val avatarBlurhash: String? = null,
	val avatarColor: String? = null, // Usually null
	val isAdmin: Boolean? = null,
	val isModerator: Boolean? = null,
	val isBot: Boolean? = null,
	val isCat: Boolean? = null,
	val speakAsCat: Boolean? = null,
	val emojis: List<Emoji>,
	val onlineStatus: OnlineStatus? = null,
	val driveCapacityOverrideMb: Int? = null,
)

fun UserLite.toDomain(fetchedFromInstance: String): Profile = Profile(
	id = id,
	name = name,
	username = username,
	instance = host ?: fetchedFromInstance,
	fullUsername = if (username.contains('@')) username else "${username}@${host ?: fetchedFromInstance}",
	avatarUrl = avatarUrl,
	avatarBlur = avatarBlurhash,
	emojis = emojis.associate { Pair(it.name, it.toDomain(host ?: fetchedFromInstance)) },

	// Below is extra
	headerUrl = null,
	headerBlur = null,
	following = null,
	followers = null,
	postCount = null,
	createdAt = null,
	fields = emptyList(),
	description = null,
)
