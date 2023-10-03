package sh.elizabeth.wastodon.data.datasource

import sh.elizabeth.wastodon.api.firefish.TimelineFirefishApi
import sh.elizabeth.wastodon.api.firefish.model.toDomain
import sh.elizabeth.wastodon.api.mastodon.TimelineMastodonApi
import sh.elizabeth.wastodon.api.mastodon.model.toDomain
import sh.elizabeth.wastodon.model.Post
import sh.elizabeth.wastodon.util.SupportedInstances
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
