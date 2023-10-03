package sh.elizabeth.fedihome.data.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.model.ProfileField

data class FullProfile(
	@Embedded val profile: ProfileEntity,

	@Embedded val extra: ProfileExtraEntity?,
)

data class EnrichedFullProfile(
	@Embedded val profile: ProfileEntity,

	@Embedded val extra: ProfileExtraEntity?,

	@Relation(
		parentColumn = "profileId",
		entityColumn = "fullEmojiId",
		associateBy = Junction(ProfileEmojiCrossRef::class)
	) var profileEmojis: List<EmojiEntity>,
)

fun FullProfile.toDomain(emojis: List<EmojiEntity>) = Profile(
	id = profile.profileId,
	name = profile.name,
	username = profile.username,
	instance = profile.instance,
	fullUsername = profile.fullUsername,
	avatarUrl = profile.avatarUrl,
	avatarBlur = profile.avatarBlur,
	headerUrl = extra?.headerUrl,
	headerBlur = extra?.headerBlur,
	following = extra?.following,
	followers = extra?.followers,
	postCount = extra?.postCount,
	createdAt = extra?.createdAt,
	fields = extra?.fields?.map {
		ProfileField(
			it.name, it.value
		)
	} ?: emptyList(),
	description = extra?.description,
	emojis = emojis.associate { Pair(it.shortcode, it.toDomain()) },
)

fun EnrichedFullProfile.toDomain() = FullProfile(
	profile = profile,
	extra = extra,
).toDomain(profileEmojis)
