package sh.elizabeth.wastodon.domain

import sh.elizabeth.wastodon.data.repository.AuthRepository
import sh.elizabeth.wastodon.data.repository.ProfileRepository
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
