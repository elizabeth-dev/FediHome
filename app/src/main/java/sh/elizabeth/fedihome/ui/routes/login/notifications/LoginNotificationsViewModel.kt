package sh.elizabeth.fedihome.ui.routes.login.notifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sh.elizabeth.fedihome.data.repository.InternalDataRepository
import sh.elizabeth.fedihome.model.NotificationType
import javax.inject.Inject

data class LoginNotificationsUiState(
	val savedSettings: Boolean = false,
)

@HiltViewModel
class LoginNotificationsViewModel @Inject constructor(internalDataRepository: InternalDataRepository) :
	ViewModel() {

	private val _uiState = MutableStateFlow(LoginNotificationsUiState())
	val uiState = _uiState.asStateFlow()

	fun saveSettings(
		enabledNotifications: Boolean,
		notificationTypes: Map<NotificationType, Boolean>,
	) {
		_uiState.update { it.copy(savedSettings = true) }
	}
}