package sh.elizabeth.fedihome.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedihome.ProfileEmojiCrossRef
import sh.elizabeth.fedihome.ProfileEntity
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.database.entity.ProfileFieldEntity
import sh.elizabeth.fedihome.data.database.entity.toDomain
import sh.elizabeth.fedihome.model.Profile
import javax.inject.Inject

class ProfileLocalDataSource @Inject constructor(private val appDatabase: AppDatabase) {
	fun insertOrReplace(vararg profiles: Profile) {
			profiles.forEach { profile ->
				appDatabase.profileQueries.insertOrReplace(profile.toEntity())
			}
	}

	fun insertOrReplaceEmojiCrossRef(
		vararg refs: ProfileEmojiCrossRef,
	) = appDatabase.profileQueries.transaction {
		refs.forEach { ref ->
			appDatabase.profileQueries.insertOrReplaceProfileEmojiCrossRef(ref)
		}
	}

	fun getById(profileId: String): Flow<Profile?> =
		appDatabase.profileQueries.getProfileById(profileId)
			.asFlow()
			.mapToOneOrNull(Dispatchers.IO)
			.map { it?.toDomain() }

	fun getMultipleById(profileIds: List<String>): Flow<List<Profile>> =
		appDatabase.profileQueries.getMultipleProfilesByIds(profileIds)
			.asFlow()
			.mapToList(Dispatchers.IO)
			.map { profiles -> profiles.map { it.toDomain() } }
}

fun Profile.toEntity() = ProfileEntity(
	profileId = id,
	name = name,
	username = username,
	instance = instance,
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
