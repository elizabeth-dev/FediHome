package sh.elizabeth.fedihome.ui.view.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.domain.GetTimelineUseCase
import sh.elizabeth.fedihome.domain.RefreshTimelineUseCase
import sh.elizabeth.fedihome.domain.VotePollUseCase
import sh.elizabeth.fedihome.model.Post
import javax.inject.Inject

sealed interface HomeUiState {
	val isLoading: Boolean
	val activeAccount: String

	data class NoPosts(
		override val isLoading: Boolean,
		override val activeAccount: String,
	) : HomeUiState

	data class HasPosts(
		val posts: List<Post>,
		override val isLoading: Boolean,
		override val activeAccount: String,
	) : HomeUiState
}

private data class HomeViewModelState(
	val isLoading: Boolean = false,
) {
	fun toUiState(posts: List<Post>?, activeAccount: String = ""): HomeUiState =
		if (posts.isNullOrEmpty()) {
			HomeUiState.NoPosts(isLoading = isLoading, activeAccount = activeAccount)
		} else {
			HomeUiState.HasPosts(
				posts = posts,
				isLoading = isLoading,
				activeAccount = activeAccount
			)
		}

}

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val getTimelineUseCase: GetTimelineUseCase,
	authRepository: AuthRepository,
	private val refreshTimelineUseCase: RefreshTimelineUseCase,
	private val votePollUseCase: VotePollUseCase,
) : ViewModel() {
	private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

	@OptIn(ExperimentalCoroutinesApi::class)
	private val timeline =
		authRepository.activeAccount.flatMapLatest { getTimelineUseCase(it) }.distinctUntilChanged()

	val uiState = combine(
		viewModelState,
		authRepository.activeAccount,
		timeline
	) { state, activeAccount, posts ->
		state.toUiState(posts, activeAccount)
	}.stateIn(
		viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState(
			posts = null,
		)
	)

	fun refreshTimeline(profileIdentifier: String) {
		viewModelState.update { it.copy(isLoading = true) }
		viewModelScope.launch {
			refreshTimelineUseCase(profileIdentifier)
			viewModelState.update { it.copy(isLoading = false) }
		}
	}

	fun votePoll(profileIdentifier: String, postId: String, pollId: String?, choices: List<Int>) {
		viewModelScope.launch {
			votePollUseCase(profileIdentifier, postId, pollId, choices)
		}
	}
}
