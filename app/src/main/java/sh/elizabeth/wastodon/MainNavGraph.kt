package sh.elizabeth.wastodon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import sh.elizabeth.wastodon.ui.view.home.HomeRoute
import sh.elizabeth.wastodon.ui.view.login.LoginRoute
import sh.elizabeth.wastodon.util.APP_DEEPLINK_URI

const val TOKEN_PARAM = "token"

@Composable
fun MainNavGraph(
	navController: NavHostController = rememberNavController(),
	startDestination: String = MainDestinations.HOME_ROUTE,
	navActions: MainNavigationActions = remember(navController) {
		MainNavigationActions(navController)
	},
) {
	NavHost(startDestination = startDestination, navController = navController) {
		composable(MainDestinations.HOME_ROUTE) {
			HomeRoute(navToLogin = navActions.navigateToLogin)
		}
		composable(
			route = MainDestinations.LOGIN_ROUTE,
			deepLinks = listOf(navDeepLink {
				uriPattern =
					"$APP_DEEPLINK_URI/${MainDestinations.LOGIN_ROUTE}?token={$TOKEN_PARAM}"
			})
		) {
			LoginRoute(navToHome = navActions.navigateToHome)
		}

	}

}
