package sh.elizabeth.fedihome.ui.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun PostFAB(onClick: () -> Unit) {
	FloatingActionButton(onClick = onClick) {
		Icon(Icons.Outlined.Add, contentDescription = "New post")
	}
}
