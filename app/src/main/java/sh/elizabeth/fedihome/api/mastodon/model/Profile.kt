package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.util.InstantAsString
import sh.elizabeth.fedihome.model.Profile as DomainProfile
import sh.elizabeth.fedihome.model.ProfileField as DomainProfileField

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
	@SerialName("avatar_static") val avatarStatic: String? = null,
	val header: String? = null,
	@SerialName("header_static") val headerStatic: String? = null,
	val locked: Boolean,
	val fields: List<ProfileField>,
	val emojis: List<Emoji>,
	val bot: Boolean,
	val group: Boolean? = null,
	val discoverable: Boolean? = null,
	val noindex: Boolean? = null,
	val moved: Profile? = null,
	val suspended: Boolean? = null,
	val limited: Boolean? = null,
	@SerialName("created_at") val createdAt: InstantAsString,
	@SerialName("last_status_at") val lastStatusAt: String? = null,
	@SerialName("statuses_count") val statusesCount: Int,
	@SerialName("followers_count") val followersCount: Int,
	@SerialName("following_count") val followingCount: Int,
	@SerialName("mute_expired_at") val muteExpiredAt: InstantAsString? = null,
	// Check roles field on a mod
	// From Iceshrimp.NET
	val fqn: String? = null,
)

fun Profile.toDomain(fetchedFromInstance: String): DomainProfile {
	val instance = acct.split('@').getOrElse(1) { fetchedFromInstance }

	return DomainProfile(
		id = "$id@$fetchedFromInstance",
		name = displayName,
		username = if (acct.contains('@')) acct else "$acct@$instance",
		instance = instance,
//		fullUsername = if (acct.contains('@')) acct else "$acct@$instance",
		avatarUrl = avatar,
		avatarBlur = null,
		headerUrl = header,
		headerBlur = null,
		following = followingCount.toLong(),
		followers = followersCount.toLong(),
		postCount = statusesCount.toLong(),
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
