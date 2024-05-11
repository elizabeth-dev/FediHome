package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class PushSubscriptionRequest(
	val subscription: PushSubscriptionRequest_Subscription,
	val data: PushSubscriptionRequest_Data,
)

@Serializable data class PushSubscriptionRequest_Subscription(
	val endpoint: String,
	val keys: PushSubscriptionRequest_Keys,
)

@Serializable data class PushSubscriptionRequest_Keys(
	val p256dh: String,
	val auth: String,
)

@Serializable enum class PushSubscriptionRequest_Policy {
	@SerialName("all") ALL,

	@SerialName("followed") FOLLOWED,

	@SerialName("follower") FOLLOWER,

	@SerialName("none") NONE,
}

@Serializable data class PushSubscriptionRequest_Data(
	val alerts: PushSubscriptionRequest_Alerts,
	val policy: PushSubscriptionRequest_Policy,
)

@Serializable data class PushSubscriptionRequest_Alerts(
	val mention: Boolean = false,
	val status: Boolean = false,
	val reblog: Boolean = false,
	val follow: Boolean = false,
	@SerialName("follow_request") val followRequest: Boolean = false,
	val favourite: Boolean = false,
	val poll: Boolean = false,
	val update: Boolean = false,
	@SerialName("admin.sign_up") val adminSignUp: Boolean = false,
	@SerialName("admin.report") val adminReport: Boolean = false,
) {
	companion object {
		val ALL = PushSubscriptionRequest_Alerts(
			mention = true,
			status = true,
			reblog = true,
			follow = true,
			followRequest = true,
			favourite = true,
			poll = true,
			update = true,
			adminSignUp = true,
			adminReport = true,
		)
	}
}

@Serializable data class PushSubscriptionResponse(
	val id: Int,
	val endpoint: String,
	val alerts: PushSubscriptionRequest_Alerts,
	@SerialName("server_key") val serverKey: String,
	val policy: PushSubscriptionRequest_Policy,
)