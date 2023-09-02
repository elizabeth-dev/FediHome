package sh.elizabeth.wastodon.ui.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import sh.elizabeth.wastodon.data.repository.AuthRepository
import javax.inject.Inject

sealed interface HomeUiState {
	val isLoading: Boolean

	data class LoadingAuth(
		override val isLoading: Boolean,
	) : HomeUiState

	data class NoAuth(
		override val isLoading: Boolean,
	) : HomeUiState

	data class NoPosts(
		override val isLoading: Boolean,
		val activeAccount: String,
	) : HomeUiState

	data class HasPosts(
		val posts: List<String>,
		override val isLoading: Boolean,
		val activeAccount: String,
	) : HomeUiState
}

private data class HomeViewModelState(
	val posts: List<String>? = null,
	val isLoading: Boolean = false,
) {
	fun toUiState(activeAccount: String, loadedAccount: Boolean = false): HomeUiState = if (activeAccount == "") {

		if (loadedAccount) {
			HomeUiState.NoAuth(isLoading = false)
		} else {
			HomeUiState.LoadingAuth(isLoading = true)
		}
	} else if (posts == null) {
		HomeUiState.NoPosts(isLoading = isLoading, activeAccount = activeAccount)
	} else {
		HomeUiState.HasPosts(posts = posts, isLoading = isLoading, activeAccount = activeAccount)
	}

}

@HiltViewModel
class HomeViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
	private val _activeAccount = authRepository.activeAccount
	private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

	val uiState = combine(viewModelState, _activeAccount) { viewModelState, activeAccount ->
		viewModelState.toUiState(activeAccount, true)
	}
		.stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState(""))
}
