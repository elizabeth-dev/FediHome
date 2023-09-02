package sh.elizabeth.wastodon

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import sh.elizabeth.wastodon.ui.theme.WastodonTheme

@Composable
fun WastodonApp(windowSizeClass: WindowSizeClass) {
    WastodonTheme {
        val navController = rememberNavController()

        MainNavGraph(navController = navController, windowSizeClass = windowSizeClass)
    }
}
