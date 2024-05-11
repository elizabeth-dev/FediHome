package sh.elizabeth.fedihome.api.sharkey.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class PushNotification_Subscription_Request(
	val endpoint: String,
	val publickey: String,
	val auth: String,
	val sendReadMessage: Boolean = false,
	val i: String,
)

enum class PushNotification_Subscription_State {
	@SerialName("subscribed") SUBSCRIBED,
	@SerialName("already-subscribed") ALREADY_SUBSCRIBED,
}

@Serializable data class PushNotification_Subscription_Response(
	val state: PushNotification_Subscription_State,
	val key: String,
	val userId: String,
	val endpoint: String,
	val sendReadMessage: Boolean,
)

@Serializable data class PushNotification_Unregister(
	val endpoint: String,
)
