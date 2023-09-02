package sh.elizabeth.wastodon.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import sh.elizabeth.wastodon.model.Profile

@Entity(
	indices = [
		// TODO: Maybe add an Index for fullUsername? or stop storing fullUsername?
		Index(
			value = ["instance", "accountId"],
			unique = true
		)
	]
)
data class ProfileEntity(
	@PrimaryKey(autoGenerate = true) var id: Long = 0,
	val accountId: String,
	val name: String?,
	val username: String,
	val instance: String,
	val fullUsername: String,
	val avatarUrl: String?,
	val headerUrl: String?,
)

fun ProfileEntity.toDomain() = Profile(
	id = accountId,
	name = name,
	username = username,
	instance = instance,
	fullUsername = fullUsername,
	avatarUrl = avatarUrl,
	headerUrl = headerUrl
)
