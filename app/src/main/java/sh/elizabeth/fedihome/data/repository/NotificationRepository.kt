package sh.elizabeth.fedihome.data.repository

import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.data.datasource.NotificationLocalDataSource
import sh.elizabeth.fedihome.data.datasource.NotificationRemoteDataSource
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class NotificationRepository @Inject constructor(
	private val notificationLocalDataSource: NotificationLocalDataSource,
	private val notificationRemoteDataSource: NotificationRemoteDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {
	private suspend fun getInstanceAndTypeAndToken(activeAccount: String): Triple<String, SupportedInstances, String> =
		activeAccount.let {
			val internalData = internalDataLocalDataSource.internalData.first()
			val instance = it.split('@')[1]
			Triple(instance, internalData.serverTypes[instance]!!, internalData.accessTokens[it]!!)
		}

	suspend fun fetchNotifications(activeAccount: String) {
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(activeAccount)

		val notificationRes = notificationRemoteDataSource.getNotifications(
			activeAccount, instance, instanceType, token
		)

		notificationLocalDataSource.insertOrReplace(*notificationRes.toTypedArray())
	}

	fun getNotificationsFlow(activeAccount: String) =
		notificationLocalDataSource.getNotificationsFlow(activeAccount)
}
