package sh.elizabeth.fedihome.ui.routes.login.oauth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginOAuthRoute(
	loginOAuthViewModel: LoginOAuthViewModel = hiltViewModel(),
	navToDashboard: () -> Unit,
	navBack: () -> Unit,
) {
	loginOAuthViewModel.initialize()

	val uiState by loginOAuthViewModel.uiState.collectAsStateWithLifecycle()

	LoginOAuthScreen(
		uiState = uiState,
		navToDashboard = navToDashboard,
		onLogin = loginOAuthViewModel::getLoginUrl,
		navBack = navBack,
	)
}
