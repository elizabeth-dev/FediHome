package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.api.firefish.TimelineFirefishApi
import sh.elizabeth.fedihome.api.firefish.model.toDomain
import sh.elizabeth.fedihome.api.mastodon.TimelineMastodonApi
import sh.elizabeth.fedihome.api.mastodon.model.toDomain
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class TimelineRemoteDataSource @Inject constructor(
	private val timelineFirefishApi: TimelineFirefishApi,
	private val timelineMastodonApi: TimelineMastodonApi,
) {
	suspend fun getHome(
		instance: String,
		instanceType: SupportedInstances,
		token: String,
	): List<Post> = when (instanceType) {
		SupportedInstances.FIREFISH -> timelineFirefishApi.getHome(instance, token)
			.map { it.toDomain(instance) }

		SupportedInstances.GLITCH,
		SupportedInstances.MASTODON,
		-> timelineMastodonApi.getHome(instance, token).map { it.toDomain(instance) }
	}
}
