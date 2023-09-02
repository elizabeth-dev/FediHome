package sh.elizabeth.wastodon.data.datasource

import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.map
import sh.elizabeth.wastodon.Settings
import sh.elizabeth.wastodon.copy
import sh.elizabeth.wastodon.data.model.SettingsData
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class SettingsSerializer @Inject constructor() : Serializer<Settings> {
	override val defaultValue: Settings = Settings.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): Settings {
		try {
			return Settings.parseFrom(input)
		} catch (exception: InvalidProtocolBufferException) {
			throw CorruptionException("Cannot read proto.", exception)
		}
	}

	override suspend fun writeTo(
		t: Settings, output: OutputStream,
	) = t.writeTo(output)
}

class SettingsLocalDataSource @Inject constructor(
	private val settings: DataStore<Settings>,
) {
	val settingsData =
		settings.data.map { SettingsData(it.appSecretsMap, it.lastLoginInstance, it.accessTokensMap, it.activeAccount) }

	suspend fun addAppSecret(instance: String, appSecret: String) {
		try {
			settings.updateData {
				it.copy {
					appSecrets.put(instance, appSecret)
				}
			}
		} catch (ioException: IOException) {
			Log.e("SettingsLocalDataSource", "Failed to update settings", ioException)
		}
	}

	suspend fun setLastLoginInstance(instance: String) {
		try {
			settings.updateData {
				it.copy {
					lastLoginInstance = instance
				}
			}
		} catch (ioException: IOException) {
			Log.e("SettingsLocalDataSource", "Failed to update settings", ioException)
		}
	}

	suspend fun addAccessToken(identifier: String, accessToken: String) {
		try {
			settings.updateData {
				it.copy {
					accessTokens.put(identifier, accessToken)
				}
			}
		} catch (ioException: IOException) {
			Log.e("SettingsLocalDataSource", "Failed to update settings", ioException)
		}
	}

	suspend fun setActiveAccount(identifier: String) {
		try {
			settings.updateData {
				it.copy {
					activeAccount = identifier
				}
			}
		} catch (ioException: IOException) {
			Log.e("SettingsLocalDataSource", "Failed to update settings", ioException)
		}
	}
}
