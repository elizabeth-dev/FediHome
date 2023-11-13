package sh.elizabeth.fedihome.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.mock.defaultProfile
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPicker(
	isVisible: Boolean,
	profiles: List<Profile>,
	onSwitch: (profileId: String) -> Unit,
	onDismiss: () -> Unit,
	onAddProfile: () -> Unit,
) {
	val sheetState = rememberModalBottomSheetState()
	val scope = rememberCoroutineScope()

	if (isVisible) ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
		profiles.forEach { profile ->
			SlimProfileSummary(
				profile = profile, onClick = {
					onSwitch(profile.id)
					scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
				}, modifier = Modifier
					.padding(horizontal = 16.dp, vertical = 16.dp)
					.fillMaxWidth()
			)
		}
		Divider()
		TextButton(
			onClick = {
				onAddProfile()
				scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
			},
			modifier = Modifier.fillMaxWidth(),
			shape = RectangleShape,

			) {
			Text(text = "Add new account")
		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun AccountPickerPreview() {
	var isVisible by remember { mutableStateOf(true) }

	FediHomeTheme {
		AccountPicker(isVisible = isVisible,
			profiles = listOf(defaultProfile, defaultProfile, defaultProfile),
			onSwitch = {},
			onDismiss = { isVisible = false; isVisible = true },
			onAddProfile = {})
	}
}
