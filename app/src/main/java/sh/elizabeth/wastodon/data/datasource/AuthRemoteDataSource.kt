package sh.elizabeth.wastodon.data.datasource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import sh.elizabeth.wastodon.data.model.*
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(private val httpClient: HttpClient) {
    suspend fun createApp(
        instance: String,
        name: String,
        description: String,
        permission: List<String>,
        callbackUrl: String
    ): CreateAppResponse = httpClient.post(Url("https://$instance/api/app/create")) {
        contentType(ContentType.Application.Json)
        setBody(
            CreateAppRequest(
                name = name,
                description = description,
                permission = permission,
                callbackUrl = callbackUrl
            )
        )
    }.body()

    suspend fun generateSession(instance: String, appSecret: String): GenerateSessionResponse = httpClient.post(
        Url("https://$instance/api/auth/session/generate")
    ) {
        contentType(ContentType.Application.Json)
        setBody(GenerateSessionRequest(appSecret = appSecret))
    }.body()

    suspend fun getUserKey(instance: String, appSecret: String, token: String): UserKeyResponse = httpClient.post(
        Url("https://$instance/api/auth/session/userkey")
    ) {
        contentType(ContentType.Application.Json)
        setBody(UserKeyRequest(appSecret = appSecret, token = token))
    }.body()
}
