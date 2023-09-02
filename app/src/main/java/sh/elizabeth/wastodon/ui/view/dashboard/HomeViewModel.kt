package sh.elizabeth.wastodon.ui.view.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import sh.elizabeth.wastodon.data.repository.AuthRepository
import javax.inject.Inject

sealed interface HomeUiState {
	val isLoading: Boolean

	data class NoPosts(
		override val isLoading: Boolean,
	) : HomeUiState

	data class HasPosts(
		val posts: List<String>,
		override val isLoading: Boolean,
	) : HomeUiState
}

private data class HomeViewModelState(
	val posts: List<String>? = null,
	val isLoading: Boolean = false,
) {
	fun toUiState(): HomeUiState = if (posts == null) {
		HomeUiState.NoPosts(isLoading = isLoading)
	} else {
		HomeUiState.HasPosts(posts = posts, isLoading = isLoading)
	}

}

@HiltViewModel
class HomeViewModel @Inject constructor(authRepository: AuthRepository) : ViewModel() {
	private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

	val uiState =
		viewModelState
			.map(HomeViewModelState::toUiState)
			.stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())
}
