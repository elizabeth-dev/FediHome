package sh.elizabeth.fedihome.domain

import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.data.repository.ProfileRepository
import javax.inject.Inject

class FinishOAuthUseCase @Inject constructor(
	private val authRepository: AuthRepository,
	private val profileRepository: ProfileRepository,
) {
	suspend operator fun invoke(token: String) {
		val user = authRepository.finishOAuth(token)
		profileRepository.insertOrReplace(user)
	}
}
