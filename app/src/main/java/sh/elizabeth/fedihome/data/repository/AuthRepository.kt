package sh.elizabeth.fedihome.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedihome.data.datasource.AuthRemoteDataSource
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.data.datasource.MetaRemoteDataSource
import sh.elizabeth.fedihome.model.Profile
import javax.inject.Inject

class AuthRepository @Inject constructor(
	private val authRemoteDataSource: AuthRemoteDataSource,
	private val metaRemoteDataSource: MetaRemoteDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {
	val internalData = internalDataLocalDataSource.internalData

	val activeAccount = internalDataLocalDataSource.internalData.map { it.activeAccount }

	val loggedInAccounts = internalDataLocalDataSource.internalData.map { it.accessTokens.keys }

	suspend fun prepareOAuth(instance: String): String {
		internalDataLocalDataSource.setLastLoginInstance(instance)

		val instanceType = metaRemoteDataSource.getInstanceType(instance)
			?: throw IllegalArgumentException("Instance type not supported")

		internalDataLocalDataSource.addServerType(instance, instanceType)

		return authRemoteDataSource.prepareOAuth(instance, instanceType)
	}

	suspend fun finishOAuth(token: String): Profile {
		val settingsData = internalDataLocalDataSource.internalData.first()
		val instance =
			if (settingsData.lastLoginInstance != "") settingsData.lastLoginInstance else throw IllegalStateException(
				"Last login instance not found"
			)

		val instanceType = settingsData.serverTypes[instance]
			?: throw IllegalStateException("Instance type not found")

		val (accessToken, profile) = authRemoteDataSource.finishOAuth(instance, instanceType, token)

		internalDataLocalDataSource.addAccessToken(profile.id, accessToken)
		internalDataLocalDataSource.setActiveAccount(profile.id)

		return profile
	}

	suspend fun setActiveAccount(profileId: String) {
		internalDataLocalDataSource.setActiveAccount(profileId)
	}
}
