package sh.elizabeth.wastodon.data.datasource

import sh.elizabeth.wastodon.api.firefish.MetaFirefishApi
import sh.elizabeth.wastodon.api.mastodon.MetaMastodonApi
import sh.elizabeth.wastodon.util.SupportedInstances
import javax.inject.Inject

class MetaRemoteDataSource @Inject constructor(
	private val metaMastodonApi: MetaMastodonApi,
	private val metaFirefishApi: MetaFirefishApi,
) {
	suspend fun getInstanceType(instance: String): SupportedInstances? {
		val firefishRes = metaFirefishApi.getPing(instance)
		if (firefishRes) return SupportedInstances.FIREFISH

		val mastodonRes = metaMastodonApi.getInstance(instance)
		if (mastodonRes != null) {
			return if (mastodonRes.version.contains("glitch")) SupportedInstances.GLITCH
			else SupportedInstances.MASTODON
		}

		return null
	}
}
