package sh.elizabeth.fedihome.data.datasource

import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.MainDestinations
import sh.elizabeth.fedihome.api.firefish.AuthFirefishApi
import sh.elizabeth.fedihome.api.firefish.model.toDomain
import sh.elizabeth.fedihome.api.mastodon.AuthMastodonApi
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.api.sharkey.AuthSharkeyApi
import sh.elizabeth.fedihome.api.sharkey.model.toDomain
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.util.APP_DEEPLINK_URI
import sh.elizabeth.fedihome.util.MASTODON_APP_PERMISSION
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
	private val authMastodonApi: AuthMastodonApi,
	private val authFirefishApi: AuthFirefishApi,
	private val authSharkeyApi: AuthSharkeyApi,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {

	suspend fun prepareOAuth(
		instance: String,
		instanceType: SupportedInstances,
	): String {
		when (instanceType) {
			SupportedInstances.FIREFISH, SupportedInstances.SHARKEY -> {
				val appSecret =
					internalDataLocalDataSource.internalData.first().instances[instance]?.appSecret.takeUnless { it.isNullOrBlank() }
						?: authFirefishApi.createApp(instance = instance).secret.also { appSecret ->
							internalDataLocalDataSource.setInstance(
								instance = instance,
								newInstanceType = null,
								newAppSecret = appSecret,
								newAppId = null
							)
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
				val cachedApp =
					internalDataLocalDataSource.internalData.first().let {
						val _instance = it.instances[instance]
						_instance?.appId to _instance?.appSecret
					}

				val appData =
					if (!cachedApp.first.isNullOrBlank() && !cachedApp.second.isNullOrBlank()) cachedApp else authMastodonApi.createApp(
						instance = instance,
					)
						.let { it.clientId to it.clientSecret }
						.also { (clientId, clientSecret) ->
							internalDataLocalDataSource.setInstance(
								instance = instance,
								newInstanceType = null,
								newAppId = clientId,
								newAppSecret = clientSecret
							)
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
				val appSecret =
					internalData.instances[instance]?.appSecret
						?: throw IllegalStateException("App secret for $instance not found")

				val userKey =
					authFirefishApi.getUserKey(instance, appSecret, token)

				return userKey.accessToken to userKey.user.toDomain(instance)
			}

			SupportedInstances.SHARKEY -> {
				val appSecret =
					internalData.instances[instance]?.appSecret
						?: throw IllegalStateException("App secret for $instance not found")

				val userKey =
					authSharkeyApi.getUserKey(instance, appSecret, token)

				return userKey.accessToken to userKey.user.toDomain(instance)
			}

			SupportedInstances.GLITCH,
			SupportedInstances.MASTODON,
			-> {
				val (appId, appSecret) = let {
					val _instance = internalData.instances[instance]
					_instance?.appId to _instance?.appSecret
				}

				if (appId == null || appSecret == null) {
					throw IllegalStateException("App data for $instance not found")
				}

				val accessToken = authMastodonApi.getAccessToken(
					instance, token, appId, appSecret
				).access_token

				val profile =
					authMastodonApi.verifyCredentials(instance, accessToken)
						.toDomain(instance)

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
