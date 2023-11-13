package sh.elizabeth.fedihome.ui.view.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.data.repository.ProfileRepository
import sh.elizabeth.fedihome.model.Profile
import javax.inject.Inject

sealed interface DashboardUiState {

	data object Loading : DashboardUiState

	data object NotLoggedIn : DashboardUiState

	data class LoggedIn(val loggedInProfiles: List<Profile>, val activeAccount: String) :
		DashboardUiState
}

private data class DashboardViewModelState(
	val isAuthLoading: Boolean = true,
	val loggedInProfiles: List<Profile>? = null,
	val activeAccount: String? = null,
) {
	fun toUiState(): DashboardUiState = if (isAuthLoading) {
		DashboardUiState.Loading
	} else if (!loggedInProfiles.isNullOrEmpty() && !activeAccount.isNullOrBlank()) {
		DashboardUiState.LoggedIn(loggedInProfiles, activeAccount)
	} else {
		DashboardUiState.NotLoggedIn
	}
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
	private val authRepository: AuthRepository,
	profileRepository: ProfileRepository,
) : ViewModel() {
	private val viewModelState = MutableStateFlow(DashboardViewModelState(isAuthLoading = true))

	val uiState = viewModelState.map(DashboardViewModelState::toUiState)
		.stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

	init {
		viewModelScope.launch {
			authRepository.internalData.map {
				if (it.accessTokens.isEmpty()) Pair(it.activeAccount, emptyList())
				else Pair(
					it.activeAccount,
					profileRepository.getMultipleByIds(it.accessTokens.keys.toList())
				)
			}.collect { pair ->
				viewModelState.update {
					it.copy(
						activeAccount = pair.first,
						loggedInProfiles = pair.second,
						isAuthLoading = false
					)
				}
			}
		}
	}

	fun switchActiveProfile(profileId: String) {
		if (profileId == viewModelState.value.activeAccount) return

		viewModelScope.launch {
			authRepository.setActiveAccount(profileId)
		}
	}
}
