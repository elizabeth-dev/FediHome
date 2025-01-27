package sh.elizabeth.fedihome.api.nodeinfo

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import sh.elizabeth.fedihome.api.nodeinfo.model.NodeInfo
import sh.elizabeth.fedihome.api.nodeinfo.model.NodeInfoSoftware
import sh.elizabeth.fedihome.api.nodeinfo.model.WellKnownNodeInfo
import java.net.URI
import javax.inject.Inject

class NodeInfoApi @Inject constructor(private val httpClient: HttpClient) {
    suspend fun getDelegatedInstanceData(instance: String): Pair<String, String>? =
        httpClient.get("https://$instance/.well-known/nodeinfo").let {
                if (it.status.isSuccess()) it.body<WellKnownNodeInfo>().links.map { URI(it.href).authority to it.href }
                    .firstOrNull() else null
            }

    suspend fun getSoftware(nodeInfoHref: String): NodeInfoSoftware? =
        httpClient.get(nodeInfoHref).let {
            if (it.status.isSuccess()) it.body<NodeInfo>().software else null
        }

}
