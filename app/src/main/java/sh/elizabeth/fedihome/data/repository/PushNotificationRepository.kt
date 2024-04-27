package sh.elizabeth.fedihome.data.repository

import android.content.Context
import androidx.work.Data
import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.data.datasource.PushNotificationLocalDataSource
import sh.elizabeth.fedihome.data.datasource.PushNotificationRemoteDataSource
import sh.elizabeth.fedihome.util.serializePublicKey
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("EC")

val rngGenerator: SecureRandom = SecureRandom()

class PushNotificationRepository @Inject constructor(
	private val pushNotificationRemoteDataSource: PushNotificationRemoteDataSource,
	private val pushNotificationLocalDataSource: PushNotificationLocalDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
	private val internalDataRepository: InternalDataRepository,
) {
	init {
		keyPairGenerator.initialize(ECGenParameterSpec("prime256v1"))
	}

	suspend fun updatePushTokenAll(deviceToken: String) {
		val accounts =
			internalDataLocalDataSource.internalData.first().accounts.keys

		for (account in accounts) {
			updatePushToken(account, deviceToken)
		}
	}

	@OptIn(ExperimentalEncodingApi::class) suspend fun updatePushToken(
		accountIdentifier: String,
		deviceToken: String,
	) {
		internalDataLocalDataSource.setFcmDeviceToken(deviceToken)

		val (instance, instanceType, accessToken) = internalDataRepository.getInstanceAndTypeAndToken(
			accountIdentifier
		)

		// Generate cryptographic stuff
		val keyPair = keyPairGenerator.generateKeyPair()
		val encodedPublicKey = Base64.UrlSafe.encode(
			serializePublicKey(keyPair.public)
		) // FIXME: no padding

		val authKey = ByteArray(16)
		rngGenerator.nextBytes(authKey)

		val pushAccountId = ByteArray(16)
		rngGenerator.nextBytes(pushAccountId)

		// Start registering
		internalDataLocalDataSource.setPushData(
			accountIdentifier = accountIdentifier,
			newPublicKey = Base64.UrlSafe.encode(keyPair.public.encoded),
			newPrivateKey = Base64.UrlSafe.encode(keyPair.private.encoded),
			newAuthKey = Base64.UrlSafe.encode(authKey),
			newPushAccountId = Base64.UrlSafe.encode(pushAccountId)
		)

		pushNotificationRemoteDataSource.deletePushSubscription(
			instance, instanceType, accessToken
		)
		pushNotificationRemoteDataSource.registerPushSubscription(
			instance,
			instanceType,
			accessToken,
			deviceToken,
			Base64.UrlSafe.encode(pushAccountId),
			encodedPublicKey,
			Base64.UrlSafe.encode(authKey)
		)

	}

	suspend fun handleIncomingNotification(
		appContext: Context,
		pushAccountId: String,
		notificationData: Data,
	) {
		val (accountId, account) = internalDataRepository.getAccountByPushAccountId(
			pushAccountId
		) ?: return
		val (instance, instanceType, _) = internalDataRepository.getInstanceAndTypeAndToken(
			accountId
		)

		pushNotificationLocalDataSource.handleNotification(
			accountId = accountId,
			instance = instance,
			instanceType = instanceType,
			account = account,
			messageData = notificationData,
			context = appContext
		)
	}
}