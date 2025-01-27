package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.api.nodeinfo.NodeInfoApi
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class MetaRemoteDataSource @Inject constructor(
	private val nodeInfoApi: NodeInfoApi,
) {
	suspend fun getInstanceData(instance: String): Pair<String, SupportedInstances>? {
		val (delegatedInstance, nodeInfoHref) = nodeInfoApi.getDelegatedInstanceData(instance) ?: return null
		val nodeInfoSoftware = nodeInfoApi.getSoftware(nodeInfoHref) ?: return null

		return delegatedInstance to when (nodeInfoSoftware.name) {
			"mastodon" -> if (nodeInfoSoftware.version.contains("glitch")) SupportedInstances.GLITCH else SupportedInstances.MASTODON
			"iceshrimp" -> SupportedInstances.FIREFISH
			"sharkey" -> SupportedInstances.SHARKEY
			else -> return null
		}

		return null
	}
}
