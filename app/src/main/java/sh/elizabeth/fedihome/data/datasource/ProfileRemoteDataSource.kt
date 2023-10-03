package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.api.firefish.ProfileFirefishApi
import sh.elizabeth.fedihome.api.firefish.model.toDomain
import sh.elizabeth.fedihome.api.mastodon.ProfileMastodonApi
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
	private val profileMastodonApi: ProfileMastodonApi,
	private val profileFirefishApi: ProfileFirefishApi,
) {
	suspend fun fetchProfile(
		instance: String,
		instanceType: SupportedInstances,
		token: String,
		profileId: String,
	): Profile = when (instanceType) {
		SupportedInstances.FIREFISH -> profileFirefishApi.fetchProfile(instance, token, profileId)
			.toDomain(instance)

		SupportedInstances.GLITCH,
		SupportedInstances.MASTODON,
		-> profileMastodonApi.fetchProfile(instance, token, profileId).toDomain(instance)
	}
}
