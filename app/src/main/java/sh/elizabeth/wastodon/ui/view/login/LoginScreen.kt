package sh.elizabeth.wastodon.ui.view.login

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.wastodon.ui.theme.WastodonTheme
import sh.elizabeth.wastodon.util.openLinkInCustomTab

val padding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(uiState: LoginUiState, navToDashboard: () -> Unit, onLogin: (String) -> Unit) {
	val context = LocalContext.current
	val (instance, setInstance) = remember { mutableStateOf("") }

	if (uiState.oauthUrl != null) {
		openLinkInCustomTab(Uri.parse(uiState.oauthUrl), context)
	}

	if (uiState.successfulLogin) {
		navToDashboard()
		return
	}

	Surface(Modifier.fillMaxSize()) {
		Column(
			Modifier.wrapContentSize(Alignment.Center),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			if (uiState.isLoading) {
				CircularProgressIndicator()
			} else {
				TextField(
					value = instance,
					onValueChange = { setInstance(it) },
					label = { Text("Instance") },
					singleLine = true,
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
					keyboardActions = KeyboardActions(onGo = { onLogin(instance) })
				)
				Spacer(Modifier.size(padding))
				Button(modifier = Modifier.align(Alignment.End), onClick = { onLogin(instance) }) {
					Text("Login", style = MaterialTheme.typography.labelLarge)
				}
			}
		}
	}
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun LoginScreenPreview() {
	WastodonTheme {
		LoginScreen(
			uiState = LoginUiState(isLoading = false, errorMessage = "Login failed"),
			navToDashboard = {},
			onLogin = {})
	}
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun LoginScreenLoadingPreview() {
	WastodonTheme {
		LoginScreen(
			uiState = LoginUiState(isLoading = true, errorMessage = "Login failed"),
			navToDashboard = {},
			onLogin = {})
	}
}
