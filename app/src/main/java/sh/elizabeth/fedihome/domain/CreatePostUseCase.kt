package sh.elizabeth.fedihome.domain

import sh.elizabeth.fedihome.data.repository.PostRepository
import sh.elizabeth.fedihome.model.PostDraft
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(private val postRepository: PostRepository) {
	suspend operator fun invoke(profileIdentifier: String, newPost: PostDraft) =
		postRepository.createPost(profileIdentifier, newPost)
}
