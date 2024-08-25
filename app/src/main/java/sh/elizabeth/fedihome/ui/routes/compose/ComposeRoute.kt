package sh.elizabeth.fedihome.ui.routes.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ComposeRoute(composeViewModel: ComposeViewModel = hiltViewModel(), onFinish: () -> Unit) {
	val uiState by composeViewModel.uiState.collectAsStateWithLifecycle()
	ComposeRoute(
		uiState = uiState,
		onSendPost = { text, cw ->
			composeViewModel.sendPost(text, cw)
			onFinish()
		},
		onClose = onFinish,
		onSwitchActiveProfile = { composeViewModel.switchActiveProfile(it, uiState.activeAccount) })
}

@Composable
fun ComposeRoute(
	uiState: ComposeUiState,
	onSendPost: (String, String?) -> Unit,
	onClose: () -> Unit,
	onSwitchActiveProfile: (profileId: String) -> Unit,
) {
	ComposeScreen(
		uiState = uiState,
		onSendPost = onSendPost,
		onClose = onClose,
		onSwitchActiveProfile = onSwitchActiveProfile
	)
}
