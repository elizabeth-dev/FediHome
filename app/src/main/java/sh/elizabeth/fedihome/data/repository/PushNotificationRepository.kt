package sh.elizabeth.fedihome.data.repository

import android.content.Context
import androidx.work.Data
import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.data.datasource.PushNotificationLocalDataSource
import sh.elizabeth.fedihome.data.datasource.PushNotificationRemoteDataSource
import javax.inject.Inject

class PushNotificationRepository @Inject constructor(
	private val pushNotificationRemoteDataSource: PushNotificationRemoteDataSource,
	private val pushNotificationLocalDataSource: PushNotificationLocalDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
	private val internalDataRepository: InternalDataRepository,
) {

	suspend fun updatePushTokenAll(deviceToken: String) {
		val accounts = internalDataLocalDataSource.internalData.first().accounts

		for (account in accounts) {
			updatePushToken(
				account.key, deviceToken, account.value.pushData.pushEndpoint
			)
		}
	}

	suspend fun updatePushToken(
		accountIdentifier: String,
		deviceToken: String,
		oldEndpoint: String? = null,
	) {
		internalDataLocalDataSource.setFcmDeviceToken(deviceToken)

		val (instance, instanceType, accessToken) = internalDataRepository.getInstanceAndTypeAndToken(
			accountIdentifier
		)

		if (!oldEndpoint.isNullOrBlank()) pushNotificationRemoteDataSource.deletePushSubscription(
			instance, instanceType, accessToken, oldEndpoint
		)

		val pushData =
			pushNotificationRemoteDataSource.registerPushSubscription(
				instance,
				instanceType,
				accessToken,
				deviceToken,
			)

		internalDataLocalDataSource.setPushData(
			accountIdentifier = accountIdentifier,
			newPublicKey = pushData.publicKey,
			newPrivateKey = pushData.privateKey,
			newServerKey = pushData.serverKey,
			newAuthSecret = pushData.authSecret,
			newPushAccountId = pushData.pushAccountId,
			newPushEndpoint = pushData.endpoint,
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