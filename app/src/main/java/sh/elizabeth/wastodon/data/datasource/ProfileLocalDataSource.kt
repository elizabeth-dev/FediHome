package sh.elizabeth.wastodon.data.datasource

import sh.elizabeth.wastodon.data.database.dao.ProfileDao
import sh.elizabeth.wastodon.data.database.entity.ProfileEntity
import sh.elizabeth.wastodon.data.database.entity.toDomain
import sh.elizabeth.wastodon.model.Profile
import javax.inject.Inject

class ProfileLocalDataSource @Inject constructor(private val profileDao: ProfileDao) {
	suspend fun insertOrReplace(vararg profiles: Profile): List<Long> =
		profileDao.insertOrReplace(*profiles.map { it.toEntity() }.toTypedArray())

	suspend fun getByInstanceAndAccountId(instance: String, accountId: String): Profile? =
		profileDao.getByInstanceAndAccountId(instance, accountId)?.toDomain()
}

fun Profile.toEntity() = ProfileEntity(
	accountId = id,
	name = name,
	username = username,
	instance = instance,
	fullUsername = fullUsername,
	avatarUrl = avatarUrl,
	headerUrl = headerUrl,
)
