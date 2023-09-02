package sh.elizabeth.wastodon.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import sh.elizabeth.wastodon.data.datasource.SettingsLocalDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpModule {

	@OptIn(ExperimentalSerializationApi::class)
	@Singleton
	@Provides
	fun provideHttpClient(settingsLocalDataSource: SettingsLocalDataSource): HttpClient = HttpClient {
		install(ContentNegotiation) {
			json(Json {
				ignoreUnknownKeys = true
				encodeDefaults = true
				explicitNulls = false
			})
		}
		install(Auth) {
			bearer {

				loadTokens {
					val settings = settingsLocalDataSource.settingsData.first()
					val token = settings.accessTokens[settings.activeAccount]

					if (token.isNullOrBlank()) null else BearerTokens(token, "")
				}
			}
		}
	}
}
