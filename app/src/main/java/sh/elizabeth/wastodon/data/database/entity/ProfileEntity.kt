package sh.elizabeth.wastodon.data.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.model.ProfileField
import java.time.Instant

@Entity(
	indices = [
		// TODO: Maybe add an Index for fullUsername? or stop storing fullUsername?
		Index(
			value = ["instance", "profileId"], unique = true
		), Index(
			value = ["profileId"],
			unique = true // FIXME: not actually unique, although calckey api might return a unique id. check this.
		)]
)
data class ProfileEntity(
	@PrimaryKey(autoGenerate = true) var profileRow: Long = 0,
	val profileId: String,
	val name: String?,
	val username: String,
	val instance: String,
	val fullUsername: String,
	val avatarUrl: String?,
	val avatarBlur: String?,
)

@Entity(
	foreignKeys = [ForeignKey(
		entity = ProfileEntity::class,
		parentColumns = ["profileId"],
		childColumns = ["profileRef"],
	)], indices = [Index(
		value = ["profileRef"], unique = true
	)]
)
data class ProfileExtraEntity(
	@PrimaryKey(autoGenerate = true) var profileExtraRow: Long = 0,
	val profileRef: String?,
	val headerUrl: String?,
	val headerBlur: String?,
	val description: String?,
	val following: Int?,
	val followers: Int?,
	val postCount: Int?,
	val createdAt: Instant?,
	val fields: List<ProfileFieldEntity>,
)

data class ProfileFieldEntity(
	val name: String,
	val value: String,
)

data class FullProfileEntity(
	@Embedded val profile: ProfileEntity,

	@Embedded val extra: ProfileExtraEntity,
)

fun FullProfileEntity.toDomain() = Profile(
	id = profile.profileId,
	name = profile.name,
	username = profile.username,
	instance = profile.instance,
	fullUsername = profile.fullUsername,
	avatarUrl = profile.avatarUrl,
	avatarBlur = profile.avatarBlur,
	headerUrl = extra.headerUrl,
	headerBlur = extra.headerBlur,
	following = extra.following,
	followers = extra.followers,
	postCount = extra.postCount,
	createdAt = extra.createdAt,
	fields = extra.fields.map {
		ProfileField(
			it.name, it.value
		)
	},
	description = extra.description,
)
