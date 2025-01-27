package sh.elizabeth.fedihome.data.datasource

import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedihome.Account
import sh.elizabeth.fedihome.AccountPushData
import sh.elizabeth.fedihome.Instance
import sh.elizabeth.fedihome.InternalData
import sh.elizabeth.fedihome.copy
import sh.elizabeth.fedihome.data.datasource.model.InternalDataAccount
import sh.elizabeth.fedihome.data.datasource.model.InternalDataAccount_PushData
import sh.elizabeth.fedihome.data.datasource.model.InternalDataInstance
import sh.elizabeth.fedihome.data.datasource.model.InternalDataValues
import sh.elizabeth.fedihome.util.SupportedInstances
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class InternalDataSerializer @Inject constructor() : Serializer<InternalData> {
	override val defaultValue: InternalData = InternalData.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): InternalData {
		try {
			return InternalData.parseFrom(input)
		} catch (exception: InvalidProtocolBufferException) {
			throw CorruptionException("Cannot read proto.", exception)
		}
	}

	override suspend fun writeTo(
		t: InternalData, output: OutputStream,
	) = t.writeTo(output)
}

class InternalDataLocalDataSource @Inject constructor(
	private val internalDataDataStore: DataStore<InternalData>,
) {
	val internalData = internalDataDataStore.data.map {
		InternalDataValues(
			lastLoginInstance = it.lastLoginInstance,
			activeAccount = it.activeAccount,
			accounts = it.accountsMap.map { (key, value) ->
				key to InternalDataAccount(
					accessToken = value.accessToken,
					pushData = InternalDataAccount_PushData(
						pushPublicKey = value.pushData.pushPublicKey,
						pushPrivateKey = value.pushData.pushPrivateKey,
						pushServerKey = value.pushData.pushServerKey,
						pushAuthSecret = value.pushData.pushAuthSecret,
						pushAccountId = value.pushData.pushAccountId,
						pushEndpoint = value.pushData.pushEndpoint,
					)
				)
			}.toMap(),
			instances = it.instancesMap.map { (key, value) ->
				key to InternalDataInstance(
					instanceType = SupportedInstances.valueOf(value.instanceType),
					appId = value.appId,
					appSecret = value.appSecret,
					delegatedEndpoint = value.delegatedEndpoint
				)
			}.toMap(),
			fcmDeviceToken = it.fcmDeviceToken,
		)
	}

	suspend fun setLastLoginInstance(instance: String) {
		try {
			internalDataDataStore.updateData {
				it.copy {
					lastLoginInstance = instance
				}
			}
		} catch (ioException: IOException) {
			Log.e(
				"InternalDataLocalDataSource",
				"Failed to update settings",
				ioException
			)
		}
	}

	suspend fun setActiveAccount(identifier: String) {
		try {
			internalDataDataStore.updateData {
				it.copy {
					activeAccount = identifier
				}
			}
		} catch (ioException: IOException) {
			Log.e(
				"InternalDataLocalDataSource",
				"Failed to update settings",
				ioException
			)
		}
	}

	suspend fun setAccessToken(
		accountIdentifier: String,
		newAccessToken: String,
	) {
		try {
			val currentAccount =
				internalDataDataStore.data.map { it.accountsMap[accountIdentifier] }
					.first()

			internalDataDataStore.updateData {
				it.copy {
					accounts.put(accountIdentifier, Account.newBuilder().apply {
						accessToken = newAccessToken
						pushData =
							currentAccount?.pushData
								?: AccountPushData.getDefaultInstance()
					}.build())
				}
			}
		} catch (ioException: IOException) {
			Log.e(
				"InternalDataLocalDataSource",
				"Failed to update settings",
				ioException
			)
		}
	}

	suspend fun setPushData(
		accountIdentifier: String,
		newPublicKey: String,
		newPrivateKey: String,
		newServerKey: String?,
		newAuthSecret: String?,
		newPushAccountId: String,
		newPushEndpoint: String,
	) {
		try {
			val currentAccount =
				internalDataDataStore.data.map { it.accountsMap[accountIdentifier] }
					.first()

			internalDataDataStore.updateData {
				it.copy {
					accounts.put(accountIdentifier, Account.newBuilder().apply {
						accessToken = currentAccount?.accessToken
						pushData = AccountPushData.newBuilder().apply {
							pushPublicKey = newPublicKey
							pushPrivateKey = newPrivateKey
							pushServerKey = newServerKey
							pushAuthSecret = newAuthSecret
							pushAccountId = newPushAccountId
							pushEndpoint = newPushEndpoint
						}.build()
					}.build())
				}
			}
		} catch (ioException: IOException) {
			Log.e(
				"InternalDataLocalDataSource",
				"Failed to update settings",
				ioException
			)
		}
	}

	suspend fun setInstance(
		instance: String,
		newDelegatedEndpoint: String?,
		newInstanceType: SupportedInstances?,
		newAppId: String?,
		newAppSecret: String?,
	) {
		try {
			val currentInstance =
				internalDataDataStore.data.map { it.instancesMap[instance] }
					.first()

			internalDataDataStore.updateData {
				it.copy {
					instances.put(instance, Instance.newBuilder().apply {
						instanceType =
							newInstanceType?.name
								?: currentInstance?.instanceType
						delegatedEndpoint = newDelegatedEndpoint ?: currentInstance?.delegatedEndpoint
						appId = newAppId ?: currentInstance?.appId ?: ""
						appSecret =
							newAppSecret ?: currentInstance?.appSecret ?: ""
					}.build())
				}
			}
		} catch (ioException: IOException) {
			Log.e(
				"InternalDataLocalDataSource",
				"Failed to update settings",
				ioException
			)
		}
	}

	suspend fun setFcmDeviceToken(token: String) {
		try {
			internalDataDataStore.updateData {
				it.copy {
					fcmDeviceToken = token
				}
			}
		} catch (ioException: IOException) {
			Log.e(
				"InternalDataLocalDataSource",
				"Failed to update settings",
				ioException
			)
		}
	}
}
