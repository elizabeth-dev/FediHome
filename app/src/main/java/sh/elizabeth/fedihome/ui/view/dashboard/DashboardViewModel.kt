package sh.elizabeth.fedihome.ui.view.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

fun toUiState(
	activeAccount: String? = null,
	loggedInProfiles: List<Profile>? = null,
	isAuthLoading: Boolean = true,
): DashboardUiState = if (isAuthLoading) {
	DashboardUiState.Loading
} else if (!loggedInProfiles.isNullOrEmpty() && !activeAccount.isNullOrBlank()) {
	DashboardUiState.LoggedIn(loggedInProfiles, activeAccount)
} else {
	DashboardUiState.NotLoggedIn
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
	private val authRepository: AuthRepository,
	profileRepository: ProfileRepository,
) : ViewModel() {

	@OptIn(ExperimentalCoroutinesApi::class)
	val uiState = authRepository.internalData.flatMapLatest { authData ->
		profileRepository.getMultipleByIdsFlow(authData.accessTokens.keys.toList()).map {
			toUiState(
				activeAccount = authData.activeAccount, loggedInProfiles = it, isAuthLoading = false
			)
		}
	}.stateIn(viewModelScope, SharingStarted.Eagerly, DashboardUiState.Loading)

	fun switchActiveProfile(profileId: String, activeAccount: String?) {
		if (profileId == activeAccount) return

		viewModelScope.launch {
			authRepository.setActiveAccount(profileId)
		}
	}
}
