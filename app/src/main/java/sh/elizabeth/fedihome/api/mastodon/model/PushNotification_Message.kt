package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.model.PushNotification
import sh.elizabeth.fedihome.model.PushNotificationType

@Serializable enum class PushNotification_Type {
	@SerialName("mention") MENTION,

	@SerialName("reblog") REBLOG,

	@SerialName("status") STATUS,

	@SerialName("follow") FOLLOW,

	@SerialName("follow_request") FOLLOW_REQUEST,

	@SerialName("favourite") FAVOURITE,

	@SerialName("poll") POLL,

	@SerialName("update") UPDATE,

	@SerialName("admin.sign_up") ADMIN_SIGN_UP,

	@SerialName("admin.report") ADMIN_REPORT,
}

fun PushNotification_Type.toDomain(): PushNotificationType {
	return when (this) {
		PushNotification_Type.MENTION -> PushNotificationType.MENTION
		PushNotification_Type.REBLOG -> PushNotificationType.REBLOG
		PushNotification_Type.STATUS -> PushNotificationType.STATUS
		PushNotification_Type.FOLLOW -> PushNotificationType.FOLLOW
		PushNotification_Type.FOLLOW_REQUEST -> PushNotificationType.FOLLOW_REQUEST
		PushNotification_Type.FAVOURITE -> PushNotificationType.FAVOURITE
		PushNotification_Type.POLL -> PushNotificationType.POLL
		PushNotification_Type.UPDATE -> PushNotificationType.UPDATE
		PushNotification_Type.ADMIN_SIGN_UP -> PushNotificationType.ADMIN_SIGN_UP
		PushNotification_Type.ADMIN_REPORT -> PushNotificationType.ADMIN_REPORT
	}
}

@Serializable data class PushNotification_Message(
	val access_token: String,
	val preferred_locale: String,
	val notification_id: Int,
	val notification_type: PushNotification_Type,
	val icon: String,
	val title: String,
	val body: String,
)

fun PushNotification_Message.toDomain(
	accountId: String,
	fromInstance: String,
): PushNotification {
	return PushNotification(
		accountIdentifier = accountId,
		id = "$notification_id@$fromInstance",
		type = notification_type.toDomain(),
		icon = icon,
		title = title,
		body = body,
	)
}