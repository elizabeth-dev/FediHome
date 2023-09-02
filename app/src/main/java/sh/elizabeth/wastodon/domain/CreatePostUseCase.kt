package sh.elizabeth.wastodon.domain

import sh.elizabeth.wastodon.data.repository.PostRepository
import sh.elizabeth.wastodon.model.PostDraft
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(private val postRepository: PostRepository) {
	suspend operator fun invoke(profileIdentifier: String, newPost: PostDraft) =
		postRepository.createPost(profileIdentifier.split(':')[0], newPost)
}
