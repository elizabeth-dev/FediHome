package sh.elizabeth.fedihome.data.datasource

import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedihome.InternalData
import sh.elizabeth.fedihome.copy
import sh.elizabeth.fedihome.util.SupportedInstances
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

data class InternalDataValues(
	val appIds: Map<String, String>,
	val appSecrets: Map<String, String>,
	val lastLoginInstance: String,
	val accessTokens: Map<String, String>,
	val serverTypes: Map<String, SupportedInstances>,
	val activeAccount: String,
)

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
			appIds = it.appIdsMap,
			appSecrets = it.appSecretsMap,
			lastLoginInstance = it.lastLoginInstance,
			accessTokens = it.accessTokensMap,
			activeAccount = it.activeAccount,
			serverTypes = it.serverTypesMap.map { (key, value) ->
				key to SupportedInstances.valueOf(value)
			}.toMap(),
		)
	}

	suspend fun addAppId(instance: String, appId: String) {
		try {
			internalDataDataStore.updateData {
				it.copy {
					appIds.put(instance, appId)
				}
			}
		} catch (ioException: IOException) {
			Log.e("InternalDataLocalDataSource", "Failed to update settings", ioException)
		}
	}

	suspend fun addAppSecret(instance: String, appSecret: String) {
		try {
			internalDataDataStore.updateData {
				it.copy {
					appSecrets.put(instance, appSecret)
				}
			}
		} catch (ioException: IOException) {
			Log.e("InternalDataLocalDataSource", "Failed to update settings", ioException)
		}
	}

	suspend fun setLastLoginInstance(instance: String) {
		try {
			internalDataDataStore.updateData {
				it.copy {
					lastLoginInstance = instance
				}
			}
		} catch (ioException: IOException) {
			Log.e("InternalDataLocalDataSource", "Failed to update settings", ioException)
		}
	}

	suspend fun addAccessToken(identifier: String, accessToken: String) {
		try {
			internalDataDataStore.updateData {
				it.copy {
					accessTokens.put(identifier, accessToken)
				}
			}
		} catch (ioException: IOException) {
			Log.e("InternalDataLocalDataSource", "Failed to update settings", ioException)
		}
	}

	suspend fun addServerType(instance: String, type: SupportedInstances) {
		try {
			internalDataDataStore.updateData {
				it.copy {
					serverTypes.put(instance, type.name)
				}
			}
		} catch (ioException: IOException) {
			Log.e("InternalDataLocalDataSource", "Failed to update settings", ioException)
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
			Log.e("InternalDataLocalDataSource", "Failed to update settings", ioException)
		}
	}
}
