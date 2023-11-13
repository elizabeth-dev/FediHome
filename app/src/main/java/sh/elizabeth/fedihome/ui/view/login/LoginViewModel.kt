package sh.elizabeth.fedihome.ui.view.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.TOKEN_PARAM
import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.domain.FinishOAuthUseCase
import javax.inject.Inject

data class LoginUiState(
	val isLoading: Boolean = false,
	val oauthUrl: String? = null,
	val errorMessage: String? = null,
	val successfulLogin: Boolean = false,
	val canNavBack: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val authRepository: AuthRepository,
	private val finishOAuthUseCase: FinishOAuthUseCase,
) : ViewModel() {

	private val _uiState = MutableStateFlow(LoginUiState(isLoading = false))
	val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			if (savedStateHandle.contains(TOKEN_PARAM)) {
				_uiState.update { it.copy(isLoading = true) }
				onTokenReceived(savedStateHandle.get<String>(TOKEN_PARAM)!!)
				_uiState.update { it.copy(successfulLogin = true, isLoading = false) }
			} else {
				_uiState.update { it.copy(isLoading = true) }
				val loggedIn = authRepository.loggedInAccounts.first().isNotEmpty()

				_uiState.update {
					if (loggedIn) {
						it.copy(canNavBack = true, isLoading = false)
					} else {
						it.copy(isLoading = false)
					}
				}
			}
		}
	}

	fun getLoginUrl(instance: String) {
		_uiState.update { it.copy(isLoading = true) }
		viewModelScope.launch {
			val oauthUrl = authRepository.prepareOAuth(instance)
			_uiState.update { it.copy(oauthUrl = oauthUrl) }
		}
	}

	private suspend fun onTokenReceived(token: String) {
		// TODO: Handle errors here
		finishOAuthUseCase(token)
	}
}
