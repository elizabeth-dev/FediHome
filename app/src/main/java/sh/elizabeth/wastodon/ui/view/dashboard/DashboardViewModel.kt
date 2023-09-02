package sh.elizabeth.wastodon.ui.view.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import sh.elizabeth.wastodon.data.repository.AuthRepository
import javax.inject.Inject

data class DashboardUiState(
	val isAuthLoading: Boolean = true,
	val isLoggedIn: Boolean = false,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(authRepository: AuthRepository) :
	ViewModel() {
	private val _uiState = MutableStateFlow(DashboardUiState())
	val uiState = combine(
		_uiState,
		authRepository.activeAccount,
	) { uiState, activeAccount ->
		uiState.copy(isAuthLoading = false, isLoggedIn = activeAccount != "")
	}.stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value)
}
