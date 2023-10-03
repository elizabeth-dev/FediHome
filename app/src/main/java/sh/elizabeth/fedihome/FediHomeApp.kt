package sh.elizabeth.fedihome

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun FediHomeApp(windowSizeClass: WindowSizeClass) {
	FediHomeTheme {
		val navController = rememberNavController()

		MainNavGraph(navController = navController, windowSizeClass = windowSizeClass)
	}
}
