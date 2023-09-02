package sh.elizabeth.wastodon.ui.view.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ComposeRoute(composeViewModel: ComposeViewModel = hiltViewModel(), onFinish: () -> Unit) {
	val uiState by composeViewModel.uiState.collectAsStateWithLifecycle()
	ComposeRoute(uiState = uiState, onSendPost = { text, cw ->
		composeViewModel.sendPost(text, cw)
		onFinish()
	}, onClose = onFinish)
}

@Composable
fun ComposeRoute(
	uiState: ComposeUiState,
	onSendPost: (String, String?) -> Unit,
	onClose: () -> Unit,
) {
	ComposeScreen(uiState = uiState, onSendPost = onSendPost, onClose = onClose)
}
