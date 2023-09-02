package sh.elizabeth.wastodon.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.util.InstantAsString

@Serializable
enum class OnlineStatus {
	@SerialName("unknown")
	UNKNOWN,

	@SerialName("online")
	ONLINE,

	@SerialName("active")
	ACTIVE,

	@SerialName("offline")
	OFFLINE,
}

@Serializable
enum class FollowsFollowersVisibility {
	@SerialName("followers")
	FOLLOWERS,

	@SerialName("public")
	PUBLIC,

	@SerialName("private")
	PRIVATE,
}

@Serializable
data class UserDetailedNotMe(
	val id: String,
	val name: String? = null,
	val username: String,
	val host: String? = null,
	val avatarUrl: String? = null,
	val avatarBlurhash: String? = null,
	val avatarColor: String? = null,
	val isAdmin: Boolean? = null,
	val isModerator: Boolean? = null,
	val isBot: Boolean? = null,
	val isCat: Boolean? = null,
	val speakAsCat: Boolean? = null,
	val emojis: List<Emoji>,
	val onlineStatus: OnlineStatus? = null,
	val url: String? = null,
	val uri: String? = null,
	val movedToUri: String? = null,
	val alsoKnownAs: List<String>? = null,
	val createdAt: InstantAsString,
	val updatedAt: InstantAsString? = null,
	val lastFetchedAt: String? = null,
	val bannerUrl: String? = null,
	val bannerBlurhash: String? = null,
	val bannerColor: String? = null,
	val isLocked: Boolean,
	val isSilenced: Boolean,
	val isSuspended: Boolean,
	val description: String? = null,
	val location: String? = null,
	val birthday: String? = null,
	val lang: String? = null,
	val fields: List<Field>,
	val followersCount: Int,
	val followingCount: Int,
	val notesCount: Int,
	val pinnedNoteIds: List<String>,
	// TODO: should be List<Note>
	val pinnedNotes: List<String>,
	val pinnedPageId: String? = null,
	// TODO: should be Page
	val pinnedPage: String? = null,
	val publicReactions: Boolean,
	val ffVisibility: FollowsFollowersVisibility,
	val twoFactorEnabled: Boolean,
	val usePasswordLessLogin: Boolean,
	val securityKeys: Boolean,
	val isFollowing: Boolean? = null,
	val isFollowed: Boolean? = null,
	val hasPendingFollowRequestFromYou: Boolean? = null,
	val hasPendingFollowRequestToYou: Boolean? = null,
	val isBlocking: Boolean? = null,
	val isBlocked: Boolean? = null,
	val isMuted: Boolean? = null,
	val isRenoteMuted: Boolean? = null,
	val driveCapacityOverrideMb: Int? = null,
)

fun UserDetailedNotMe.toDomain(fetchedFromInstance: String): Profile = Profile(
	id = id,
	name = name,
	username = username,
	instance = host ?: fetchedFromInstance,
	fullUsername = username.contains('@')
		.let { if (it) username else "${username}@${host ?: fetchedFromInstance}" },
	avatarUrl = avatarUrl,
	headerUrl = bannerUrl,
)
