package sh.elizabeth.fedihome

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
import sh.elizabeth.fedihome.ui.routes.compose.ComposeRoute
import sh.elizabeth.fedihome.ui.routes.dashboard.DashboardRoute
import sh.elizabeth.fedihome.ui.routes.login.oauth.LoginOAuthRoute
import sh.elizabeth.fedihome.ui.routes.post.PostRoute
import sh.elizabeth.fedihome.ui.routes.profile.ProfileRoute
import sh.elizabeth.fedihome.util.APP_DEEPLINK_URI

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
				navToLogin = navActions::navigateToLoginOAuth,
				navToCompose = navActions::navigateToCompose,
				navToPost = navActions::navigateToPost,
				navToProfile = navActions::navigateToProfile,
			)
		}

		composable(
			route = MainDestinations.LOGIN_OAUTH_ROUTE,
			deepLinks = listOf(navDeepLink {
				uriPattern =
					"$APP_DEEPLINK_URI/${MainDestinations.LOGIN_OAUTH_ROUTE}?token={$TOKEN_PARAM}"
			}, navDeepLink {
				uriPattern =
					"$APP_DEEPLINK_URI/${MainDestinations.LOGIN_OAUTH_ROUTE}?code={$TOKEN_PARAM}"
			})
		) {
			LoginOAuthRoute(
				navToDashboard = navActions::navigateToDashboard,
				navBack = navActions::navigateUp
			)
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

		composable(
			route = MainDestinations.POST_ROUTE,
		) {
			PostRoute(
				navBack = navActions::navigateUp,
				navToCompose = navActions::navigateToCompose,
				navToProfile = navActions::navigateToProfile
			)
		}

		composable(
			route = MainDestinations.PROFILE_ROUTE,
		) {
			ProfileRoute(
				navBack = navActions::navigateUp,
				navToCompose = navActions::navigateToCompose,
				navToPost = navActions::navigateToPost,
				navToProfile = navActions::navigateToProfile,
			)
		}
	}

}
