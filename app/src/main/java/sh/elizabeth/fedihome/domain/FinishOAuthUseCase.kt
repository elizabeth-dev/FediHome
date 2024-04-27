package sh.elizabeth.fedihome.domain

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.data.repository.ProfileRepository
import sh.elizabeth.fedihome.data.repository.PushNotificationRepository
import javax.inject.Inject

class FinishOAuthUseCase @Inject constructor(
	private val authRepository: AuthRepository,
	private val profileRepository: ProfileRepository,
	private val pushNotificationRepository: PushNotificationRepository,
) {
	suspend operator fun invoke(oauthToken: String) {
		val user = authRepository.finishOAuth(oauthToken)
		profileRepository.insertOrReplace(user)

		// TODO: ask for push notifications activation
		// FIXME: update only the new account deviceToken
		FirebaseMessaging.getInstance().token.await()?.let {
			pushNotificationRepository.updatePushTokenAll(it)
		}
	}
}
