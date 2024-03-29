package sh.elizabeth.fedihome.ui.view.login

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme
import sh.elizabeth.fedihome.util.openLinkInCustomTab

val padding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
	uiState: LoginUiState,
	navToDashboard: () -> Unit,
	onLogin: (String) -> Unit,
	navBack: () -> Unit,
) {
	val context = LocalContext.current
	val (instance, setInstance) = remember { mutableStateOf("") }

	if (uiState.oauthUrl != null) {
		openLinkInCustomTab(Uri.parse(uiState.oauthUrl), context)
	}

	if (uiState.successfulLogin) {
		navToDashboard()
		return
	}

	Scaffold(topBar = {
		if (uiState.canNavBack && !uiState.isLoading) TopAppBar(
			title = {},
			navigationIcon = {
				IconButton(
					onClick = navBack
				) {
					Icon(
						Icons.AutoMirrored.Outlined.ArrowBack,
						contentDescription = "Back"
					)
				}
			})
	}) { contentPadding ->
		Column(
			Modifier
				.padding(contentPadding)
				.wrapContentSize(Alignment.Center)
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			if (uiState.isLoading) {
				CircularProgressIndicator()
			} else {
				Column {
					TextField(
						value = instance,
						onValueChange = { setInstance(it) },
						label = { Text("Instance") },
						singleLine = true,
						keyboardOptions = KeyboardOptions(
							imeAction = ImeAction.Go,
							keyboardType = KeyboardType.Uri
						),
						keyboardActions = KeyboardActions(onGo = {
							onLogin(instance)
						})
					)
					Spacer(Modifier.size(padding))
					Button(modifier = Modifier.align(Alignment.End),
						onClick = { onLogin(instance) }) {
						Text(
							"Login",
							style = MaterialTheme.typography.labelLarge
						)
					}
				}
			}
		}
	}
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun LoginScreenPreview() {
	FediHomeTheme {
		LoginScreen(uiState = LoginUiState(
			isLoading = false,
			errorMessage = "Login failed",
			canNavBack = true
		), navToDashboard = {}, onLogin = {}, navBack = {})
	}
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun LoginScreenLoadingPreview() {
	FediHomeTheme {
		LoginScreen(uiState = LoginUiState(
			isLoading = true,
			errorMessage = "Login failed"
		), navToDashboard = {}, onLogin = {}, navBack = {})
	}
}
