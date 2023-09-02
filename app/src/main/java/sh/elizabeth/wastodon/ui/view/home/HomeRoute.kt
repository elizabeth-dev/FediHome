package sh.elizabeth.wastodon.ui.view.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(homeViewModel: HomeViewModel = hiltViewModel(), navToLogin: () -> Unit) {
	val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

	when (uiState) {
		is HomeUiState.LoadingAuth -> {
			return
		}

		is HomeUiState.NoAuth -> {
			navToLogin()
			return
		}

		else -> HomeRoute(homeUiState = uiState)
	}
}

@Composable
fun HomeRoute(homeUiState: HomeUiState) {
	HomeScreen(uiState = homeUiState)
}
