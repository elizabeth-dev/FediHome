package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.api.iceshrimp.ProfileIceshrimpApi
import sh.elizabeth.fedihome.api.iceshrimp.model.toDomain
import sh.elizabeth.fedihome.api.mastodon.ProfileMastodonApi
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.api.sharkey.ProfileSharkeyApi
import sh.elizabeth.fedihome.api.sharkey.model.toDomain
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
	private val profileMastodonApi: ProfileMastodonApi,
	private val profileIceshrimpApi: ProfileIceshrimpApi,
	private val profileSharkeyApi: ProfileSharkeyApi,
) {
	suspend fun fetchProfile(
		instance: String,
		endpoint: String,
		instanceType: SupportedInstances,
		token: String,
		profileId: String,
	): Profile = when (instanceType) {
		SupportedInstances.ICESHRIMP -> profileIceshrimpApi.fetchProfile(
			endpoint = endpoint, token = token, profileId = profileId
		).toDomain(instance)

		SupportedInstances.SHARKEY -> profileSharkeyApi.fetchProfile(
			endpoint = endpoint, token = token, profileId = profileId
		).toDomain(instance)

		SupportedInstances.GLITCH,
		SupportedInstances.MASTODON,
			-> profileMastodonApi.fetchProfile(
			endpoint = endpoint, token = token, profileId = profileId
		).toDomain(instance)
	}

	suspend fun fetchProfileByTag(
		instance: String,
		endpoint: String,
		instanceType: SupportedInstances,
		token: String,
		profileTag: String,
	): Profile = when (instanceType) {
		SupportedInstances.ICESHRIMP -> profileIceshrimpApi.fetchProfileByTag(
			endpoint = endpoint, token = token, profileTag = profileTag
		).first().toDomain(instance)

		SupportedInstances.SHARKEY -> profileSharkeyApi.fetchProfileByTag(
			endpoint = endpoint, token = token, profileTag = profileTag
		).first().toDomain(instance)

		SupportedInstances.GLITCH,
		SupportedInstances.MASTODON,
			-> profileMastodonApi.fetchProfileByTag(
			endpoint = endpoint, token = token, profileTag = profileTag
		).toDomain(instance)
	}
}
