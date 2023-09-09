package sh.elizabeth.wastodon.data.datasource

import kotlinx.coroutines.flow.map
import sh.elizabeth.wastodon.data.database.dao.ProfileDao
import sh.elizabeth.wastodon.data.database.entity.ProfileEntity
import sh.elizabeth.wastodon.data.database.entity.ProfileFieldEntity
import sh.elizabeth.wastodon.data.database.entity.toDomain
import sh.elizabeth.wastodon.model.Profile
import javax.inject.Inject

class ProfileLocalDataSource @Inject constructor(private val profileDao: ProfileDao) {
	suspend fun insertOrReplace(vararg profiles: Profile): List<Long> =
		profileDao.insertOrReplace(*profiles.map(Profile::toEntity).toTypedArray())

	suspend fun getByInstanceAndProfileId(instance: String, profileId: String): Profile? =
		profileDao.getByInstanceAndProfileId(instance, profileId)?.toDomain()

	fun getProfileFlow(profileId: String) =
		profileDao.getProfileFlow(profileId).map(ProfileEntity::toDomain)
}

fun Profile.toEntity() = ProfileEntity(
	profileId = id,
	name = name,
	username = username,
	instance = instance,
	fullUsername = fullUsername,
	avatarUrl = avatarUrl,
	avatarBlur = avatarBlur,
	headerUrl = headerUrl,
	headerBlur = headerBlur,
	following = following,
	followers = followers,
	postCount = postCount,
	createdAt = createdAt,
	fields = fields.map { ProfileFieldEntity(it.name, it.value) },
	description = description,
)
