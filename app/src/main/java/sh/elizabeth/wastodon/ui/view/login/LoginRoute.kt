package sh.elizabeth.wastodon.ui.view.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginRoute(loginViewModel: LoginViewModel = hiltViewModel(), navToDashboard: () -> Unit) {
	val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

	LoginRoute(uiState = uiState, navToDashboard = navToDashboard, onLogin = loginViewModel::getLoginUrl)
}

@Composable
fun LoginRoute(uiState: LoginUiState, navToDashboard: () -> Unit, onLogin: (String) -> Unit) {
	LoginScreen(
		uiState = uiState,
		navToDashboard = navToDashboard,
		onLogin = onLogin
	)
}
