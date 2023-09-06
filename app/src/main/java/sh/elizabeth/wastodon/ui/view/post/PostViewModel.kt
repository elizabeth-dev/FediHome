package sh.elizabeth.wastodon.ui.view.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sh.elizabeth.wastodon.data.repository.AuthRepository
import sh.elizabeth.wastodon.data.repository.PostRepository
import sh.elizabeth.wastodon.domain.VotePollUseCase
import sh.elizabeth.wastodon.model.Post
import javax.inject.Inject

sealed interface PostUiState {
	val isLoading: Boolean
	val activeAccount: String
	val postId: String

	data class NoPost(
		override val isLoading: Boolean,
		override val activeAccount: String,
		override val postId: String,
	) : PostUiState

	data class HasPost(
		val post: Post,
		override val isLoading: Boolean,
		override val activeAccount: String,
		override val postId: String,
	) : PostUiState
}

private data class PostViewModelState(
	val post: Post? = null,
	val isLoading: Boolean = false,
	val activeAccount: String = "",
	val postId: String,
) {
	fun toUiState(): PostUiState = if (post == null) {
		PostUiState.NoPost(isLoading = isLoading, activeAccount = activeAccount, postId = postId)
	} else {
		PostUiState.HasPost(
			post = post, isLoading = isLoading, activeAccount = activeAccount, postId = postId
		)
	}
}

@HiltViewModel
class PostViewModel @Inject constructor(
	private val votePollUseCase: VotePollUseCase,
	private val postRepository: PostRepository,
	authRepository: AuthRepository,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {
	private val postId = savedStateHandle.get<String>("postId")
		?: throw IllegalArgumentException("Post ID must be provided")

	private val viewModelState =
		MutableStateFlow(PostViewModelState(isLoading = true, postId = postId))

	val uiState = viewModelState.map(PostViewModelState::toUiState)
		.stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

	init {
		viewModelScope.launch {
			authRepository.activeAccount.collect { activeAccount ->
				refreshPost(activeAccount, postId)
				viewModelState.update {
					it.copy(activeAccount = activeAccount)
				}
			}
		}

		viewModelScope.launch {
			postRepository.getPostFlow(postId).collect { post ->
				viewModelState.update {
					it.copy(post = post)
				}
			}
		}
	}

	fun refreshPost(activeAccount: String, postId: String) {
		viewModelState.update { it.copy(isLoading = true) }
		viewModelScope.launch {
			postRepository.fetchPost(activeAccount.split(":")[0], postId)
			viewModelState.update { it.copy(isLoading = false) }
		}
	}

	fun votePoll(activeAccount: String, postId: String, choices: List<Int>) {
		viewModelScope.launch {
			votePollUseCase(activeAccount, postId, choices)
		}
	}
}
