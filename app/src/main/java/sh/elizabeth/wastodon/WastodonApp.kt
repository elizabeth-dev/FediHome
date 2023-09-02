package sh.elizabeth.wastodon

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import sh.elizabeth.wastodon.ui.theme.WastodonTheme

@Composable
fun WastodonApp() {
    WastodonTheme {
        val navController = rememberNavController()

        MainNavGraph(navController = navController)
    }
}
