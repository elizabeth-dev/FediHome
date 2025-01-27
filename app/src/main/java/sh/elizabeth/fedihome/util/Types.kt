package sh.elizabeth.fedihome.util

final data class InstanceEndpointTypeToken(
    val instance: String,
    val endpoint: String,
    val instanceType: SupportedInstances,
    val token: String,
)