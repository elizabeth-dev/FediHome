package sh.elizabeth.fedihome.api.nodeinfo.model

import kotlinx.serialization.Serializable

@Serializable
data class NodeInfo(val software: NodeInfoSoftware)

@Serializable
data class NodeInfoSoftware(val name: String, val version: String)
