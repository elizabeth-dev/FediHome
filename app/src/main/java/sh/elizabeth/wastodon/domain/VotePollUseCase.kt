package sh.elizabeth.wastodon.domain

import sh.elizabeth.wastodon.data.repository.PostRepository
import javax.inject.Inject

class VotePollUseCase @Inject constructor(private val postRepository: PostRepository) {
	suspend operator fun invoke(profileIdentifier: String, postId: String, choices: List<Int>) {
		val instance = profileIdentifier.split(':')[0]
		choices.forEach { // TODO: MMaybe parallelize this
			postRepository.votePoll(instance, postId, it)
		}
		postRepository.fetchPost(instance, postId)
	}
}
