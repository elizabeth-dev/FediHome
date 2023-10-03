package sh.elizabeth.wastodon.data.datasource

import sh.elizabeth.wastodon.api.firefish.ProfileFirefishApi
import sh.elizabeth.wastodon.api.firefish.model.toDomain
import sh.elizabeth.wastodon.api.mastodon.ProfileMastodonApi
import sh.elizabeth.wastodon.api.mastodon.model.toDomain
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.util.SupportedInstances
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
