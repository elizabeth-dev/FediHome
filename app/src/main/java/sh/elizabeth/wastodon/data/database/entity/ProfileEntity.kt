package sh.elizabeth.wastodon.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import sh.elizabeth.wastodon.model.Profile

@Entity(
	indices = [
		// TODO: Maybe add an Index for fullUsername? or stop storing fullUsername?
		Index(
			value = ["instance", "profileId"],
			unique = true
		),
		Index(
			value = ["profileId"],
			unique = true // FIXME: not actually unique, although calckey api might return a unique id. check this.
		)
	]
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
	val headerUrl: String?,
)

fun ProfileEntity.toDomain() = Profile(
	id = profileId,
	name = name,
	username = username,
	instance = instance,
	fullUsername = fullUsername,
	avatarUrl = avatarUrl,
	avatarBlur = avatarBlur,
	headerUrl = headerUrl
)
