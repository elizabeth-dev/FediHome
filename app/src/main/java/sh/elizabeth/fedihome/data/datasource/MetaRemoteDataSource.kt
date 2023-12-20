package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.api.nodeinfo.NodeInfoApi
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class MetaRemoteDataSource @Inject constructor(
	private val nodeInfoApi: NodeInfoApi,
) {
	suspend fun getInstanceType(instance: String): SupportedInstances? {
		val nodeInfoSoftware = nodeInfoApi.getSoftware(instance) ?: return null

		when (nodeInfoSoftware.name) {
			"mastodon" -> return if (nodeInfoSoftware.version.contains("glitch")) SupportedInstances.GLITCH else SupportedInstances.MASTODON
			"firefish" -> return SupportedInstances.FIREFISH
			"sharkey" -> return SupportedInstances.SHARKEY
		}

		return null
	}
}
