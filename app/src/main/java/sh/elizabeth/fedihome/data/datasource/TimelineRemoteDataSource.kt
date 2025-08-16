package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.api.iceshrimp.TimelineIceshrimpApi
import sh.elizabeth.fedihome.api.iceshrimp.model.toDomain
import sh.elizabeth.fedihome.api.mastodon.TimelineMastodonApi
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.api.sharkey.TimelineSharkeyApi
import sh.elizabeth.fedihome.api.sharkey.model.toDomain
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class TimelineRemoteDataSource @Inject constructor(
	private val timelineIceshrimpApi: TimelineIceshrimpApi,
	private val timelineSharkeyApi: TimelineSharkeyApi,
	private val timelineMastodonApi: TimelineMastodonApi,
) {
	suspend fun getHome(
		instance: String,
		endpoint: String,
		instanceType: SupportedInstances,
		token: String,
	): List<Post> = when (instanceType) {
		SupportedInstances.ICESHRIMP -> timelineIceshrimpApi.getHome(
			endpoint = endpoint, token = token
		).map { it.toDomain(instance) }

		SupportedInstances.SHARKEY -> timelineSharkeyApi.getHome(endpoint = endpoint, token = token)
			.map { it.toDomain(instance) }

		SupportedInstances.GLITCH, SupportedInstances.MASTODON, SupportedInstances.ICESHRIMPNET -> timelineMastodonApi.getHome(
			endpoint = endpoint,
			token = token
		).map { it.toDomain(instance) }
	}
}
