package sh.elizabeth.fedihome.api.nodeinfo.model

import kotlinx.serialization.Serializable

@Serializable
data class WellKnownNodeInfo(val links: List<WellKnownNodeInfoLink>)

@Serializable
data class WellKnownNodeInfoLink(val href: String, val rel: String)
