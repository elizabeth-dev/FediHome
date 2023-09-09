package sh.elizabeth.wastodon.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import sh.elizabeth.wastodon.data.repository.PostRepository
import sh.elizabeth.wastodon.data.repository.ProfileRepository
import javax.inject.Inject

class RefreshProfileAndPostsUseCase @Inject constructor(
	private val postRepository: PostRepository,
	private val profileRepository: ProfileRepository,
) {
	suspend operator fun invoke(profileIdentifier: String, profileId: String) = coroutineScope {
		val instance = profileIdentifier.split(':')[0]

		val profileDef = async { profileRepository.fetchProfile(instance, profileId) }
		val postsDef = async { postRepository.fetchPostsByProfile(instance, profileId) }

		awaitAll(profileDef, postsDef)
	}
}
