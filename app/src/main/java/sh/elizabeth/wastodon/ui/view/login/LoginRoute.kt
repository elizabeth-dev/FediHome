package sh.elizabeth.wastodon.ui.view.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginRoute(loginViewModel: LoginViewModel = hiltViewModel(), navToHome: () -> Unit) {
	val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

	LoginRoute(uiState = uiState, navToHome = navToHome, onLogin = loginViewModel::getLoginUrl)
}

@Composable
fun LoginRoute(uiState: LoginUiState, navToHome: () -> Unit, onLogin: (String) -> Unit) {
	LoginScreen(
		uiState = uiState,
		navToHome = navToHome,
		onLogin = onLogin
	)
}
