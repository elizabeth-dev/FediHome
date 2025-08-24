package sh.elizabeth.fedihome.data.database.entity

import sh.elizabeth.fedihome.GetEmojisForProfiles
import sh.elizabeth.fedihome.ProfileEntity
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.model.ProfileField

data class ProfileFieldEntity(
	val name: String,
	val value: String,
)

fun ProfileEntity.toDomain(emojiList: List<GetEmojisForProfiles>): Profile = Profile(
	id = profileId,
	username = username,
	instance = instance,
	name = name,
	description = description,
	following = following,
	followers = followers,
	postCount = postCount,
	createdAt = createdAt,
	fields = fields.map {
		ProfileField(name = it.name, value = it.value)
	},
	avatarUrl = avatarUrl,
	avatarBlur = avatarBlur,
	headerUrl = headerUrl,
	headerBlur = headerBlur,
	emojis = emojiList.associate { it.emojiId to it.toDomain() },
)
