package sh.elizabeth.fedihome.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import sh.elizabeth.fedihome.data.repository.PostRepository
import sh.elizabeth.fedihome.data.repository.ProfileRepository
import javax.inject.Inject

class RefreshProfileAndPostsUseCase @Inject constructor(
	private val postRepository: PostRepository,
	private val profileRepository: ProfileRepository,
) {
	suspend operator fun invoke(profileIdentifier: String, profileId: String) = coroutineScope {
		val profileDef = async { profileRepository.fetchProfile(profileIdentifier, profileId) }
		val postsDef = async { postRepository.fetchPostsByProfile(profileIdentifier, profileId) }

		awaitAll(profileDef, postsDef)
	}
}
