package sh.elizabeth.fedihome.api.nodeinfo

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import sh.elizabeth.fedihome.api.nodeinfo.model.NodeInfo
import sh.elizabeth.fedihome.api.nodeinfo.model.NodeInfoSoftware
import sh.elizabeth.fedihome.api.nodeinfo.model.WellKnownNodeInfo
import javax.inject.Inject

class NodeInfoApi @Inject constructor(private val httpClient: HttpClient) {
	suspend fun getSoftware(instance: String): NodeInfoSoftware? =
		httpClient.get("https://$instance/.well-known/nodeinfo")
			.let {
				if (it.status.isSuccess()) it.body<WellKnownNodeInfo>().links.firstOrNull() else null
			}
			.let { nodeInfoHref ->
				if (nodeInfoHref != null) httpClient.get(nodeInfoHref.href)
					.let {
						if (it.status.isSuccess()) it.body<NodeInfo>().software else null
					} else null
			}
}
