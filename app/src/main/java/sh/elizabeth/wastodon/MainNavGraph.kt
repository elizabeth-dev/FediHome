package sh.elizabeth.wastodon

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import sh.elizabeth.wastodon.ui.view.compose.ComposeRoute
import sh.elizabeth.wastodon.ui.view.dashboard.DashboardRoute
import sh.elizabeth.wastodon.ui.view.login.LoginRoute
import sh.elizabeth.wastodon.util.APP_DEEPLINK_URI

const val TOKEN_PARAM = "token"

@Composable
fun MainNavGraph(
	navController: NavHostController = rememberNavController(),
	startDestination: String = MainDestinations.DASHBOARD_ROUTE,
	navActions: MainNavigationActions = remember(navController) {
		MainNavigationActions(navController)
	},
	windowSizeClass: WindowSizeClass,
) {
	NavHost(startDestination = startDestination, navController = navController) {
		composable(MainDestinations.DASHBOARD_ROUTE) {
			DashboardRoute(
				windowSizeClass = windowSizeClass,
				navToLogin = navActions::navigateToLogin,
				navToCompose = navActions::navigateToCompose
			)
		}

		composable(
			route = MainDestinations.LOGIN_ROUTE, deepLinks = listOf(navDeepLink {
				uriPattern =
					"$APP_DEEPLINK_URI/${MainDestinations.LOGIN_ROUTE}?token={$TOKEN_PARAM}"
			})
		) {
			LoginRoute(navToDashboard = navActions::navigateToDashboard)
		}

		composable(
			route = "${MainDestinations.COMPOSE_ROUTE}?replyTo={replyTo}",
			arguments = listOf(navArgument("replyTo") {
				nullable = true
				type = NavType.StringType
			})
		) {
			ComposeRoute(onFinish = navActions::navigateUp)
		}
	}

}
