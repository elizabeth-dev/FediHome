package sh.elizabeth.fedihome.util.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.data.repository.PostRepository
import sh.elizabeth.fedihome.domain.VotePollUseCase

interface PostHandlingViewModel {
	val votePollUseCase: VotePollUseCase
	val postRepository: PostRepository
	val coroutineHandlingScope: CoroutineScope

	fun votePoll(activeAccount: String, postId: String, pollId: String?, choices: List<Int>) {
		coroutineHandlingScope.launch {
			votePollUseCase(activeAccount, postId, pollId, choices)
		}
	}

	fun removeFavorite(activeAccount: String, postId: String) {
		coroutineHandlingScope.launch {
			postRepository.removeFavorite(activeAccount, postId)
		}
	}

	fun addFavorite(activeAccount: String, postId: String) {
		coroutineHandlingScope.launch {
			postRepository.createFavorite(activeAccount, postId)
		}
	}

	fun removeReaction(activeAccount: String, postId: String, reaction: String) {
		coroutineHandlingScope.launch {
			postRepository.removeReaction(activeAccount, postId, reaction)
		}
	}

	fun addReaction(activeAccount: String, postId: String, reaction: String) {
		coroutineHandlingScope.launch {
			postRepository.createReaction(activeAccount, postId, reaction)
		}
	}

	fun addBoost(activeAccount: String, postId: String) {
		coroutineHandlingScope.launch {
			postRepository.createBoost(activeAccount, postId)
		}
	}

	fun removeBoost(activeAccount: String, postId: String) {
		coroutineHandlingScope.launch {
			postRepository.removeBoost(activeAccount, postId)
		}
	}
}