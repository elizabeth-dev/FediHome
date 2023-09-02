package sh.elizabeth.wastodon.data.repository

import sh.elizabeth.wastodon.data.datasource.ProfileLocalDataSource
import sh.elizabeth.wastodon.model.Profile
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val profileLocalDataSource: ProfileLocalDataSource) {
	suspend fun insertOrReplace(vararg profiles: Profile) {
		profileLocalDataSource.insertOrReplace(*profiles)
	}
}
