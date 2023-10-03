package sh.elizabeth.wastodon.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.wastodon.util.InstantAsString
import sh.elizabeth.wastodon.model.Profile as DomainProfile
import sh.elizabeth.wastodon.model.ProfileField as DomainProfileField

@Serializable
data class Profile(
	val id: String,
	val username: String,
	val acct: String,
	val url: String,
	@SerialName("display_name") val displayName: String,
	val note: String, // Needs to be cleaned up
	val uri: String? = null,
	val avatar: String? = null,
	val avatar_static: String? = null,
	val header: String? = null,
	val header_static: String? = null,
	val locked: Boolean,
	val fields: List<ProfileField>,
	val emojis: List<Emoji>,
	val bot: Boolean,
	val group: Boolean,
	val discoverable: Boolean? = null,
	val noindex: Boolean? = null,
	val moved: Profile? = null,
	val suspended: Boolean? = null,
	val limited: Boolean? = null,
	@SerialName("created_at") val createdAt: InstantAsString,
	val last_status_at: String? = null,
	@SerialName("statuses_count") val statusesCount: Int,
	@SerialName("followers_count") val followersCount: Int,
	@SerialName("following_count") val followingCount: Int,
	val mute_expired_at: InstantAsString? = null,
	// Check roles field on a mod
)

fun Profile.toDomain(fetchedFromInstance: String): DomainProfile {
	val instance = acct.split('@').getOrElse(1) { fetchedFromInstance }

	return DomainProfile(
		id = "$id@$fetchedFromInstance",
		name = displayName,
		username = username,
		instance = instance,
		fullUsername = acct,
		avatarUrl = avatar,
		avatarBlur = null,
		headerUrl = header,
		headerBlur = null,
		following = followingCount,
		followers = followersCount,
		postCount = statusesCount,
		createdAt = createdAt,
		fields = fields.map {
			DomainProfileField(
				it.name, it.value
			)
		}, // TODO: Add verified field status
		description = note,
		emojis = emojis.associate { Pair(it.shortcode, it.toDomain(instance)) },
	)
}
