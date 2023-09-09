package sh.elizabeth.wastodon.data.repository

import sh.elizabeth.wastodon.data.datasource.ProfileLocalDataSource
import sh.elizabeth.wastodon.data.datasource.ProfileRemoteDataSource
import sh.elizabeth.wastodon.data.model.toDomain
import sh.elizabeth.wastodon.model.Profile
import javax.inject.Inject

class ProfileRepository @Inject constructor(
	private val profileLocalDataSource: ProfileLocalDataSource,
	private val profileRemoteDataSource: ProfileRemoteDataSource,
) {
	suspend fun insertOrReplace(vararg profiles: Profile) {
		profileLocalDataSource.insertOrReplace(*profiles)
	}

	suspend fun insertOrReplaceMain(vararg profiles: Profile) {
		profileLocalDataSource.insertOrReplaceMain(*profiles)
	}

	suspend fun getByInstanceAndProfileId(instance: String, profileId: String): Profile? =
		profileLocalDataSource.getByInstanceAndProfileId(instance, profileId)

	fun getProfileFlow(profileId: String) = profileLocalDataSource.getProfileFlow(profileId)

	suspend fun fetchProfile(instance: String, profileId: String) {
		val profileRes =
			profileRemoteDataSource.fetchProfile(instance, profileId).toDomain(instance)

		profileLocalDataSource.insertOrReplace(profileRes)
	}
}
