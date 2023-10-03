package sh.elizabeth.fedihome.data.datasource

import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.MainDestinations
import sh.elizabeth.fedihome.api.firefish.AuthFirefishApi
import sh.elizabeth.fedihome.api.firefish.model.toDomain
import sh.elizabeth.fedihome.api.mastodon.AuthMastodonApi
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.util.APP_DEEPLINK_URI
import sh.elizabeth.fedihome.util.MASTODON_APP_PERMISSION
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
	private val authMastodonApi: AuthMastodonApi,
	private val authFirefishApi: AuthFirefishApi,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {

	suspend fun prepareOAuth(instance: String, instanceType: SupportedInstances): String {
		when (instanceType) {
			SupportedInstances.FIREFISH -> {
				val appSecret =
					internalDataLocalDataSource.internalData.first().appSecrets[instance]
						?: authFirefishApi.createApp(instance = instance).secret.also { appSecret ->
							internalDataLocalDataSource.addAppSecret(instance, appSecret)
						}
				val session = authFirefishApi.generateSession(
					instance = instance,
					appSecret = appSecret,
				)

				return session.url
			}

			SupportedInstances.GLITCH,
			SupportedInstances.MASTODON,
			-> {
				val cachedApp = internalDataLocalDataSource.internalData.first()
					.let { it.appIds[instance] to it.appSecrets[instance] }

				val appData =
					if (cachedApp.first != null && cachedApp.second != null) cachedApp else authMastodonApi.createApp(
						instance = instance,
					).let { it.clientId to it.clientSecret }.also { (clientId, clientSecret) ->
						internalDataLocalDataSource.addAppId(instance, clientId!!)
						internalDataLocalDataSource.addAppSecret(instance, clientSecret!!)
					}

				return generateAuthUrl(
					instance = instance,
					clientId = appData.first!!,
				)
			}
		}
	}

	suspend fun finishOAuth(
		instance: String,
		instanceType: SupportedInstances,
		token: String,
	): Pair<String, Profile> {
		val internalData = internalDataLocalDataSource.internalData.first()

		when (instanceType) {
			SupportedInstances.FIREFISH -> {
				val appSecret = internalData.appSecrets[instance]
					?: throw IllegalStateException("App secret for $instance not found")

				val userKey = authFirefishApi.getUserKey(instance, appSecret, token)

				return userKey.accessToken to userKey.user.toDomain(instance)
			}

			SupportedInstances.GLITCH,
			SupportedInstances.MASTODON,
			-> {
				val appId = internalData.appIds[instance]
					?: throw IllegalStateException("App id for $instance not found")
				val appSecret = internalData.appSecrets[instance]
					?: throw IllegalStateException("App secret for $instance not found")

				val accessToken = authMastodonApi.getAccessToken(
					instance, token, appId, appSecret
				).access_token

				val profile =
					authMastodonApi.verifyCredentials(instance, accessToken).toDomain(instance)

				return accessToken to profile
			}
		}
	}

	private fun generateAuthUrl(
		instance: String,
		clientId: String,
		callbackUrl: String = "$APP_DEEPLINK_URI/${MainDestinations.LOGIN_ROUTE}",
		scopes: List<String> = MASTODON_APP_PERMISSION,
	): String =
		"https://$instance/oauth/authorize?client_id=$clientId&redirect_uri=$callbackUrl&response_type=code&scope=${
			scopes.joinToString(" ")
		}"
}
