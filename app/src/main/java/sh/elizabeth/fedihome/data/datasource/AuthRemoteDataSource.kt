package sh.elizabeth.fedihome.data.datasource

import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.api.iceshrimp.AuthIceshrimpApi
import sh.elizabeth.fedihome.api.iceshrimp.model.toDomain
import sh.elizabeth.fedihome.api.mastodon.AuthMastodonApi
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.api.sharkey.AuthSharkeyApi
import sh.elizabeth.fedihome.api.sharkey.model.toDomain
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.util.APP_DEEPLINK_URI
import sh.elizabeth.fedihome.util.APP_LOGIN_OAUTH_PATH
import sh.elizabeth.fedihome.util.MASTODON_APP_PERMISSION
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
	private val authMastodonApi: AuthMastodonApi,
	private val authIceshrimpApi: AuthIceshrimpApi,
	private val authSharkeyApi: AuthSharkeyApi,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {

	suspend fun prepareOAuth(
		instance: String,
		instanceType: SupportedInstances,
	): String {
		val instanceData = internalDataLocalDataSource.internalData.first().instances[instance]
			?: throw IllegalStateException("Instance data is missing")

		when (instanceType) {
			SupportedInstances.ICESHRIMP, SupportedInstances.SHARKEY -> {
				val appSecret = instanceData.appSecret.takeUnless { it.isNullOrBlank() }
					?: authIceshrimpApi.createApp(endpoint = instanceData.delegatedEndpoint).secret.also { appSecret ->
						internalDataLocalDataSource.setInstance(
							instance = instance,
							newDelegatedEndpoint = null,
							newInstanceType = null,
							newAppSecret = appSecret,
							newAppId = null
						)
					}
				val session = authIceshrimpApi.generateSession(
					endpoint = instanceData.delegatedEndpoint,
					appSecret = appSecret,
				)

				return session.url
			}

			SupportedInstances.GLITCH, SupportedInstances.MASTODON, SupportedInstances.ICESHRIMPNET -> {
				val appData =
					if (!instanceData.appId.isNullOrBlank() && !instanceData.appSecret.isNullOrBlank()) instanceData.appId to instanceData.appSecret else authMastodonApi.createApp(
						endpoint = instanceData.delegatedEndpoint,
					).let { it.clientId to it.clientSecret }.also { (clientId, clientSecret) ->
						internalDataLocalDataSource.setInstance(
							instance = instance,
							newDelegatedEndpoint = null,
							newInstanceType = null,
							newAppId = clientId,
							newAppSecret = clientSecret
						)
					}

				return generateAuthUrl(
					endpoint = instanceData.delegatedEndpoint,
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
		val instanceData = internalDataLocalDataSource.internalData.first().instances[instance]
			?: throw IllegalStateException("Instance data is missing")

		when (instanceType) {
			SupportedInstances.ICESHRIMP -> {
				val appSecret = instanceData.appSecret
					?: throw IllegalStateException("App secret for $instance not found")

				val userKey = authIceshrimpApi.getUserKey(
					endpoint = instanceData.delegatedEndpoint, appSecret = appSecret, token = token
				)

				return userKey.accessToken to userKey.user.toDomain(instance)
			}

			SupportedInstances.SHARKEY -> {
				val appSecret = instanceData.appSecret
					?: throw IllegalStateException("App secret for $instance not found")

				val userKey = authSharkeyApi.getUserKey(
					endpoint = instanceData.delegatedEndpoint, appSecret = appSecret, token = token
				)

				return userKey.accessToken to userKey.user.toDomain(instance)
			}

			SupportedInstances.GLITCH, SupportedInstances.MASTODON, SupportedInstances.ICESHRIMPNET -> {
				val (appId, appSecret) = instanceData.appId to instanceData.appSecret

				if (appId == null || appSecret == null) {
					throw IllegalStateException("App data for $instance not found")
				}

				val accessToken = authMastodonApi.getAccessToken(
					endpoint = instanceData.delegatedEndpoint,
					code = token,
					clientId = appId,
					clientSecret = appSecret
				).access_token

				val profile = authMastodonApi.verifyCredentials(
					endpoint = instanceData.delegatedEndpoint, accessToken = accessToken
				).toDomain(instance)

				return accessToken to profile
			}
		}
	}

	private fun generateAuthUrl(
		endpoint: String,
		clientId: String,
		callbackUrl: String = "$APP_DEEPLINK_URI$APP_LOGIN_OAUTH_PATH",
		scopes: List<String> = MASTODON_APP_PERMISSION,
	): String =
		"https://$endpoint/oauth/authorize?client_id=$clientId&redirect_uri=$callbackUrl&response_type=code&scope=${
			scopes.joinToString(" ")
		}"
}
