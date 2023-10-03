package sh.elizabeth.wastodon.domain

import sh.elizabeth.wastodon.data.repository.PostRepository
import javax.inject.Inject

class VotePollUseCase @Inject constructor(private val postRepository: PostRepository) {
	suspend operator fun invoke(
		profileIdentifier: String,
		postId: String,
		pollId: String?,
		choices: List<Int>,
	) {
		postRepository.votePoll(profileIdentifier, pollId ?: postId, choices)
		postRepository.fetchPost(profileIdentifier, postId)
	}
}
