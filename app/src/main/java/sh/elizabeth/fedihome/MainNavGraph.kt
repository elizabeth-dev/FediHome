package sh.elizabeth.fedihome

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import sh.elizabeth.fedihome.ui.routes.compose.ComposeRoute
import sh.elizabeth.fedihome.ui.routes.dashboard.DashboardRoute
import sh.elizabeth.fedihome.ui.routes.login.oauth.LoginOAuthRoute
import sh.elizabeth.fedihome.ui.routes.post.PostRoute
import sh.elizabeth.fedihome.ui.routes.profile.ProfileRoute
import sh.elizabeth.fedihome.util.APP_DEEPLINK_URI
import sh.elizabeth.fedihome.util.APP_LOGIN_OAUTH_PATH

const val TOKEN_PARAM = "token"

val localNavToCompose = compositionLocalOf<(String?) -> Unit> { error("No navToCompose provided") }
val localNavToPost = compositionLocalOf<(String) -> Unit> { error("No navToPost provided") }
val localNavToProfile = compositionLocalOf<(String) -> Unit> { error("No navToProfile provided") }

@Composable
fun MainNavGraph(
	navController: NavHostController = rememberNavController(),
	startDestination: Any = MainDestinations.DASHBOARD,
	navActions: MainNavigationActions = remember(navController) {
		MainNavigationActions(navController)
	},
	windowSizeClass: WindowSizeClass,
) {
	CompositionLocalProvider(
		localNavToCompose provides navActions::navigateToCompose,
		localNavToPost provides navActions::navigateToPost,
		localNavToProfile provides navActions::navigateToProfileByTag,
	) {
		NavHost(startDestination = startDestination, navController = navController) {
			composable<MainDestinations.DASHBOARD> {
				DashboardRoute(
					windowSizeClass = windowSizeClass,
					navToLogin = navActions::navigateToLoginOAuth,
				)
			}

			composable<MainDestinations.LOGIN_OAUTH>(
				deepLinks = listOf(navDeepLink {
					uriPattern = "$APP_DEEPLINK_URI$APP_LOGIN_OAUTH_PATH?token={$TOKEN_PARAM}"
				}, navDeepLink {
					uriPattern = "$APP_DEEPLINK_URI$APP_LOGIN_OAUTH_PATH?code={$TOKEN_PARAM}"
				})
			) {
				LoginOAuthRoute(
					navToDashboard = navActions::navigateToDashboard,
					navBack = navActions::navigateUp
				)
			}

			composable<MainDestinations.COMPOSE> {
				ComposeRoute(onFinish = navActions::navigateUp)
			}

			composable<MainDestinations.POST> {
				PostRoute(
					navBack = navActions::navigateUp,
				)
			}

			composable<MainDestinations.PROFILE> {
				ProfileRoute(
					navBack = navActions::navigateUp,
				)
			}
		}
	}
}
