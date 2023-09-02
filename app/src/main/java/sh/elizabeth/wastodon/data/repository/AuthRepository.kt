package sh.elizabeth.wastodon.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import sh.elizabeth.wastodon.MainDestinations
import sh.elizabeth.wastodon.data.datasource.AuthRemoteDataSource
import sh.elizabeth.wastodon.data.datasource.SettingsLocalDataSource
import sh.elizabeth.wastodon.data.model.toDomain
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.util.APP_DEEPLINK_URI
import sh.elizabeth.wastodon.util.APP_DESCRIPTION
import sh.elizabeth.wastodon.util.APP_NAME
import sh.elizabeth.wastodon.util.APP_PERMISSION
import javax.inject.Inject

class AuthRepository @Inject constructor(
	private val authRemoteDataSource: AuthRemoteDataSource,
	private val settingsLocalDataSource: SettingsLocalDataSource,
) {
	val activeAccount = settingsLocalDataSource.settingsData.map { it.activeAccount }

	suspend fun prepareOAuth(instance: String): String {
		settingsLocalDataSource.setLastLoginInstance(instance)

		val appSecret = settingsLocalDataSource.settingsData.first().appSecrets[instance]
			?: authRemoteDataSource.createApp(
				instance = instance,
				name = APP_NAME,
				description = APP_DESCRIPTION,
				permission = APP_PERMISSION,
				callbackUrl = "$APP_DEEPLINK_URI/${MainDestinations.LOGIN_ROUTE}"
			).secret.also { appSecret ->
				settingsLocalDataSource.addAppSecret(instance, appSecret)
			}

		val session = authRemoteDataSource.generateSession(
			instance = instance,
			appSecret = appSecret,
		)

		return session.url
	}

	suspend fun finishOAuth(token: String): Profile {
		val settingsData = settingsLocalDataSource.settingsData.first()
		val instance =
			if (settingsData.lastLoginInstance != "") settingsData.lastLoginInstance else throw IllegalStateException("Last login instance not found")
		val appSecret = settingsData.appSecrets[instance]
			?: throw IllegalStateException("App secret for $instance not found")

		val userKey = authRemoteDataSource.getUserKey(instance, appSecret, token)

		val profile = userKey.user.toDomain(instance)
		val identifier = "$instance:${profile.id}"

		settingsLocalDataSource.addAccessToken(identifier, userKey.accessToken)
		settingsLocalDataSource.setActiveAccount(identifier)

		return profile
	}
}
