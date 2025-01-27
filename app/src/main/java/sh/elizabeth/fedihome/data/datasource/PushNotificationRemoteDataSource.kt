package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.api.iceshrimp.NotificationIceshrimpApi
import sh.elizabeth.fedihome.api.mastodon.NotificationMastodonApi
import sh.elizabeth.fedihome.api.sharkey.NotificationSharkeyApi
import sh.elizabeth.fedihome.util.SupportedInstances
import sh.elizabeth.fedihome.util.publicKeyToEncodedECPoint
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("EC")

val rngGenerator: SecureRandom = SecureRandom()

interface RegisterPushResult {
	val publicKey: String
	val privateKey: String
	val serverKey: String?
	val pushAccountId: String
	val authSecret: String?
	val endpoint: String

}

class PushNotificationRemoteDataSource @Inject constructor(
	private val notificationSharkeyApi: NotificationSharkeyApi,
	private val notificationIceshrimpApi: NotificationIceshrimpApi,
	private val notificationMastodonApi: NotificationMastodonApi,
) {
	init {
		keyPairGenerator.initialize(ECGenParameterSpec("prime256v1"))
	}

	@OptIn(ExperimentalEncodingApi::class)
	suspend fun registerPushSubscription(
		instance: String,
		instanceType: SupportedInstances,
		token: String,
		deviceToken: String,
	): RegisterPushResult {
		val keyPair = keyPairGenerator.generateKeyPair()
		val encodedPublicKey = Base64.UrlSafe.encode(
			publicKeyToEncodedECPoint(keyPair.public)
		) // FIXME: no padding

		val authSecret = generateAuthSecret()
		val pushAccountId = generatePushAccountId()

		when (instanceType) {
			SupportedInstances.ICESHRIMP, SupportedInstances.SHARKEY -> {
				val registerResult =
					notificationSharkeyApi.createPushSubscription(
						instance = instance,
						token = token,
						deviceToken = deviceToken,
						pushAccountId = pushAccountId,
						publicKey = encodedPublicKey,
						authSecret = authSecret
					)

				return object : RegisterPushResult {
					override val publicKey: String =
						Base64.UrlSafe.encode(keyPair.public.encoded)
					override val privateKey: String =
						Base64.UrlSafe.encode(keyPair.private.encoded)
					override val serverKey: String = registerResult.key
					override val pushAccountId: String = pushAccountId
					override val authSecret: String = authSecret
					override val endpoint: String = registerResult.endpoint
				}
			}

			SupportedInstances.GLITCH, SupportedInstances.MASTODON -> {
				val pushData = notificationMastodonApi.createPushSubscription(
					instance = instance,
					token = token,
					deviceToken = deviceToken,
					pushAccountId = pushAccountId,
					publicKey = encodedPublicKey,
					authKey = authSecret
				)

				return object : RegisterPushResult {
					override val publicKey: String =
						Base64.UrlSafe.encode(keyPair.public.encoded)
					override val privateKey: String =
						Base64.UrlSafe.encode(keyPair.private.encoded)
					override val serverKey: String = pushData.serverKey
					override val pushAccountId: String = pushAccountId
					override val authSecret: String = authSecret
					override val endpoint: String = pushData.endpoint
				}
			}
		}
	}

	@OptIn(ExperimentalEncodingApi::class)
	private fun generatePushAccountId(): String {
		val pushAccountId = ByteArray(16)
		rngGenerator.nextBytes(pushAccountId)
		return Base64.UrlSafe.encode(pushAccountId)
	}

	@OptIn(ExperimentalEncodingApi::class)
	private fun generateAuthSecret(): String {
		val authSecret = ByteArray(16)
		rngGenerator.nextBytes(authSecret)
		return Base64.UrlSafe.encode(authSecret)
	}

	suspend fun deletePushSubscription(
		instance: String,
		instanceType: SupportedInstances,
		token: String,
		oldEndpoint: String,
	) = when (instanceType) {
		SupportedInstances.ICESHRIMP, SupportedInstances.SHARKEY -> notificationSharkeyApi.deletePushSubscription(
			instance, token, oldEndpoint
		)

		SupportedInstances.GLITCH, SupportedInstances.MASTODON -> notificationMastodonApi.deletePushSubscription(
			instance, token
		)
	}
}