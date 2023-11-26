package sh.elizabeth.fedihome.ui.view.compose

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sh.elizabeth.fedihome.mock.defaultPost
import sh.elizabeth.fedihome.mock.defaultProfile
import sh.elizabeth.fedihome.ui.composable.AccountPicker
import sh.elizabeth.fedihome.ui.composable.SlimProfileCard
import sh.elizabeth.fedihome.ui.composable.TopDisclaimer
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun ComposeScreen(
	uiState: ComposeUiState,
	onSendPost: (String, String?) -> Unit,
	onClose: () -> Unit,
	onSwitchActiveProfile: (profileId: String) -> Unit,
) {
	val (postText, setPostText) = remember { mutableStateOf("") }
	val (contentWarning, setContentWarning) = remember { mutableStateOf("") }
	val (isCWVisible, setCWVisible) = remember { mutableStateOf(false) }

	val postFocus = remember { FocusRequester() }
	val cwFocus = remember { FocusRequester() }

	var showAccountPicker by remember { mutableStateOf(false) }

	// FIXME: activeAccount might not be cached?
	val activeProfile = uiState.loggedInProfiles.find { it.id == uiState.activeAccount }

	LaunchedEffect(Unit) {
		postFocus.requestFocus()
	}

	LaunchedEffect(isCWVisible) {
		if (isCWVisible) cwFocus.requestFocus()
	}

	Surface(
		Modifier
			.fillMaxSize()
			.statusBarsPadding()
			.navigationBarsPadding()
			.imePadding()
	) {
		Column {
			Surface(
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth(), tonalElevation = 6.dp
			) {
				Row(
					Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
					horizontalArrangement = Arrangement.End
				) {
					IconButton(onClick = onClose) {
						Icon(Icons.Outlined.ArrowBack, contentDescription = "Close")
					}

					Spacer(modifier = Modifier.weight(1f))

					Button(onClick = {
						onSendPost(
							postText, if (isCWVisible) contentWarning else null
						)
					}) {
						Text("Send")
					}
				}
				Divider(thickness = Dp.Hairline)
			}

			if (uiState.replyTo != null) TopDisclaimer(
				modifier = Modifier.padding(horizontal = 16.dp),
				icon = Icons.Outlined.Reply,
				iconDescription = "Reply",
				text = "Replying to ${uiState.replyTo.author.name}"
			)

			if (activeProfile != null) SlimProfileCard(
				profile = activeProfile,
				onClick = { if (uiState.loggedInProfiles.size > 1) showAccountPicker = true }
			)

			AnimatedVisibility(visible = isCWVisible) {
				TextField(
					modifier = Modifier
						.fillMaxWidth()
						.focusRequester(cwFocus),
					value = contentWarning,
					onValueChange = { setContentWarning(it) },
					placeholder = { Text("Content warning") },
					colors = TextFieldDefaults.colors(
						focusedContainerColor = MaterialTheme.colorScheme.surface,
						unfocusedContainerColor = MaterialTheme.colorScheme.surface,
						focusedIndicatorColor = Color.Transparent,
						unfocusedIndicatorColor = Color.Transparent,
					),
					shape = RectangleShape
				)
			}
			if (isCWVisible) Divider(thickness = 1.dp)

			TextField(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f)
					.focusRequester(postFocus),
				value = postText,
				onValueChange = { setPostText(it) },
				placeholder = { Text("Type what's on your mind") },
				colors = TextFieldDefaults.colors(
					focusedContainerColor = MaterialTheme.colorScheme.surface,
					unfocusedContainerColor = MaterialTheme.colorScheme.surface,
					focusedIndicatorColor = Color.Transparent,
					unfocusedIndicatorColor = Color.Transparent,
				)
			)

			Divider(thickness = Dp.Hairline)
			Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 6.dp) {
				Row(
					Modifier.padding(4.dp),
				) {
					IconButton(onClick = { /*TODO*/ }) {
						Icon(Icons.Outlined.Image, contentDescription = "Add media")
					}
					IconButton(onClick = { setCWVisible(!isCWVisible) }) {
						Icon(
							if (isCWVisible) Icons.Rounded.Warning else Icons.Rounded.WarningAmber,
							contentDescription = "Add content warning"
						)
					}
					IconButton(onClick = { /*TODO*/ }) {
						Icon(Icons.Outlined.Poll, contentDescription = "Add poll")
					}
				}
			}

			AccountPicker(isVisible = showAccountPicker,
				profiles = uiState.loggedInProfiles,
				activeProfileId = uiState.activeAccount,
				canAddProfile = false,
				onSwitch = onSwitchActiveProfile,
				onAddProfile = {},
				onDismiss = { showAccountPicker = false })
		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun ComposeScreenPreview() {
	FediHomeTheme {
		ComposeScreen(uiState = ComposeUiState(
			activeAccount = defaultProfile.id,
			loggedInProfiles = listOf(defaultProfile),
			isReply = true,
			replyTo = defaultPost
		), onSendPost = { _, _ -> }, onClose = {}, onSwitchActiveProfile = {})
	}
}
