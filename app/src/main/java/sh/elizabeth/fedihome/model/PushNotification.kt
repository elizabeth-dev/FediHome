package sh.elizabeth.fedihome.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import sh.elizabeth.fedihome.R

data class PushNotification(
	val accountIdentifier: String,
	val id: String,
	val title: String,
	val body: String,
	val icon: String,
	val type: PushNotificationType,
)

enum class PushNotificationType {
	MENTION,
	REBLOG,
	STATUS,
	FOLLOW,
	FOLLOW_REQUEST,
	FAVOURITE,
	POLL,
	UPDATE,
	ADMIN_SIGN_UP,
	ADMIN_REPORT,
	UNKNOWN
}

fun PushNotification.notify(context: Context) {
	val notification =
		NotificationCompat.Builder(context, this.accountIdentifier)
			.setSmallIcon(R.drawable.ic_stat_name)
			.setContentTitle(this.title)
			.setContentText(this.body)
			.setStyle(
				NotificationCompat.BigTextStyle().bigText(this.body)
			)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT) // FIXME: change priority
			.build()

	if (ActivityCompat.checkSelfPermission(
			context, Manifest.permission.POST_NOTIFICATIONS
		) != PackageManager.PERMISSION_GRANTED
	) {
		// TODO: Consider calling
		//    ActivityCompat#requestPermissions
		// here to request the missing permissions, and then overriding
		//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
		//                                          int[] grantResults)
		// to handle the case where the user grants the permission. See the documentation
		// for ActivityCompat#requestPermissions for more details.
		return
	}
	NotificationManagerCompat.from(context)
		.notify(
			this.id.hashCode(),
			notification
		) // FIXME: think about something else to use for id
}
