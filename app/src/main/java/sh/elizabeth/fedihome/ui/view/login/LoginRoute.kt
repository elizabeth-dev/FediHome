package sh.elizabeth.fedihome.ui.view.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginRoute(
	loginViewModel: LoginViewModel = hiltViewModel(),
	navToDashboard: () -> Unit,
	navBack: () -> Unit,
) {
	val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

	LoginScreen(
		uiState = uiState,
		navToDashboard = navToDashboard,
		onLogin = loginViewModel::getLoginUrl,
		navBack = navBack,
	)
}
