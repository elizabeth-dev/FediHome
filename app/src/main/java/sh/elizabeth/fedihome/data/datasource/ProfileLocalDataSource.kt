package sh.elizabeth.fedihome.data.datasource

import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedihome.data.database.dao.ProfileDao
import sh.elizabeth.fedihome.data.database.entity.EnrichedFullProfile
import sh.elizabeth.fedihome.data.database.entity.ProfileEmojiCrossRef
import sh.elizabeth.fedihome.data.database.entity.ProfileEntity
import sh.elizabeth.fedihome.data.database.entity.ProfileExtraEntity
import sh.elizabeth.fedihome.data.database.entity.ProfileFieldEntity
import sh.elizabeth.fedihome.data.database.entity.toDomain
import sh.elizabeth.fedihome.model.Profile
import javax.inject.Inject

class ProfileLocalDataSource @Inject constructor(private val profileDao: ProfileDao) {
	suspend fun insertOrReplace(vararg profiles: Profile): List<Long> {
		val result = profileDao.insertOrReplaceMain(*profiles.map(Profile::toEntity).toTypedArray())
		profileDao.insertOrReplaceExtra(*profiles.map(Profile::toExtraEntity).toTypedArray())

		return result
	}

	suspend fun insertOrReplaceEmojiCrossRef(vararg refs: ProfileEmojiCrossRef): List<Long> =
		profileDao.insertOrReplaceEmojiCrossRef(*refs)

	suspend fun insertOrReplaceMain(vararg profiles: Profile): List<Long> =
		profileDao.insertOrReplaceMain(*profiles.map(Profile::toEntity).toTypedArray())

	suspend fun getByInstanceAndProfileId(instance: String, profileId: String): Profile? =
		profileDao.getByInstanceAndProfileId(instance, profileId)?.toDomain()

	fun getProfileFlow(profileId: String) = profileDao.getProfileFlow(profileId)
		.filterNotNull()
		.map(EnrichedFullProfile::toDomain)
}

fun Profile.toEntity() = ProfileEntity(
	profileId = id,
	name = name,
	username = username,
	instance = instance,
	fullUsername = fullUsername,
	avatarUrl = avatarUrl,
	avatarBlur = avatarBlur,
)

fun Profile.toExtraEntity() = ProfileExtraEntity(
	profileRef = id,
	headerUrl = headerUrl,
	headerBlur = headerBlur,
	following = following,
	followers = followers,
	postCount = postCount,
	createdAt = createdAt,
	fields = fields.map { ProfileFieldEntity(it.name, it.value) },
	description = description,
)
