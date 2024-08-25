package sh.elizabeth.fedihome

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object MainDestinations {
	const val DASHBOARD_ROUTE = "dashboard"
	const val LOGIN_OAUTH_ROUTE = "login/oauth"
	const val LOGIN_NOTIFICATIONS_ROUTE = "login/notifications"
	const val COMPOSE_ROUTE = "compose"
	const val POST_ROUTE = "post/{postId}"
	const val PROFILE_ROUTE = "profile/{profileId}"

}

class MainNavigationActions(private val navController: NavHostController) {
	fun navigateToDashboard() {
		navController.navigate(MainDestinations.DASHBOARD_ROUTE) {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = false // This is needed for first login flow
			}
			launchSingleTop = true
			restoreState = true
		}
	}

	fun navigateToLoginOAuth() {
		navController.navigate(MainDestinations.LOGIN_OAUTH_ROUTE) {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}
			launchSingleTop = true
			restoreState = true
		}
	}

	fun navigateToCompose(replyTo: String? = null) {
		navController.navigate(
			if (replyTo.isNullOrBlank()) MainDestinations.COMPOSE_ROUTE else "${MainDestinations.COMPOSE_ROUTE}?replyTo=$replyTo"
		) {
			launchSingleTop = true
			restoreState = true
		}
	}

	fun navigateToPost(postId: String) {
		navController.navigate(
			MainDestinations.POST_ROUTE.replace("{postId}", postId)
		) {
			launchSingleTop = false
			restoreState = true
		}
	}

	fun navigateToProfile(profileId: String) {
		navController.navigate(
			MainDestinations.PROFILE_ROUTE.replace("{profileId}", profileId)
		) {
			launchSingleTop = false
			restoreState = true
		}
	}

	fun navigateUp() {
		navController.navigateUp()
	}
}
