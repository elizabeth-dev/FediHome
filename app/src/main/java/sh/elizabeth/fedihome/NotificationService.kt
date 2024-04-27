package sh.elizabeth.fedihome

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sh.elizabeth.fedihome.data.repository.PushNotificationRepository
import sh.elizabeth.fedihome.domain.HandlePushMessageUseCase

@HiltWorker class UpdateFcmTokenWorker @AssistedInject constructor(
	@Assisted appContext: Context,
	@Assisted private val workerParams: WorkerParameters,
	private val pushNotificationRepository: PushNotificationRepository,
) : CoroutineWorker(appContext, workerParams) {
	override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
		try {
			// TODO: do only if settings allow
			val token =
				workerParams.inputData.getString("fcmToken")
					?: return@withContext Result.failure()
			pushNotificationRepository.updatePushTokenAll(token)
			return@withContext Result.success()
		} catch (e: Exception) {
			return@withContext Result.failure()
		}
	}

}

@HiltWorker class ProcessNotificationWorker @AssistedInject constructor(
	@Assisted private val appContext: Context,
	@Assisted private val workerParams: WorkerParameters,
	private val handlePushMessageUseCase: HandlePushMessageUseCase,
	private val pushNotificationRepository: PushNotificationRepository,
) : CoroutineWorker(appContext, workerParams) {
	override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
		try {
			val pushAccountId =
				workerParams.inputData.getString("pushAccountId")
					?: return@withContext Result.failure()
			pushNotificationRepository.handleIncomingNotification(
				appContext = appContext,
				pushAccountId = pushAccountId,
				workerParams.inputData
			)
			return@withContext Result.success()
		} catch (e: Exception) {
			return@withContext Result.failure()
		}
	}

}

class NotificationService : FirebaseMessagingService() {
	override fun onNewToken(token: String) {
		WorkManager.getInstance(this).beginUniqueWork(
			"updateFcmToken",
			ExistingWorkPolicy.REPLACE,
			OneTimeWorkRequest.Builder(UpdateFcmTokenWorker::class.java)
				.setInputData(
					Data.Builder().putString("fcmToken", token).build()
				)
				.build()
		).enqueue()
	}

	override fun onMessageReceived(message: RemoteMessage) {
		val k = message.data["k"]
		val p = message.data["p"]
		val s = message.data["s"]
		val pushAccountId = message.data["x"]

		WorkManager.getInstance(this).beginUniqueWork(
			"pushNotificationReceived",
			ExistingWorkPolicy.APPEND_OR_REPLACE,
			OneTimeWorkRequest.Builder(ProcessNotificationWorker::class.java)
				.setInputData(
					Data.Builder()
						.putString("k", k)
						.putString("p", p)
						.putString("s", s)
						.putString("pushAccountId", pushAccountId)
						.build()
				)
				.build()
		).enqueue()
	}
}