package sh.elizabeth.fedihome.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpModule {

	@OptIn(ExperimentalSerializationApi::class)
	@Singleton
	@Provides
	fun provideHttpClient(): HttpClient =
		HttpClient {
			install(ContentNegotiation) {
				json(Json {
					ignoreUnknownKeys = true
					encodeDefaults = true
					explicitNulls = false
				})
			}
			install(Auth)
			install(HttpTimeout) {
				socketTimeoutMillis = 60000
			}

		}
}
