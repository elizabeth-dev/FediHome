package sh.elizabeth.wastodon

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object MainDestinations {
	const val DASHBOARD_ROUTE = "dashboard"
	const val LOGIN_ROUTE = "login"
	const val COMPOSE_ROUTE = "compose"
}

class MainNavigationActions(val navController: NavHostController) {
	fun navigateToDashboard() {
		navController.navigate(MainDestinations.DASHBOARD_ROUTE) {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = false // This is needed for first login flow
			}
			launchSingleTop = true
			restoreState = true
		}
	}

	fun navigateToLogin() {
		navController.navigate(MainDestinations.LOGIN_ROUTE) {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}
			launchSingleTop = true
			restoreState = true
		}
	}

	fun navigateToCompose(replyTo: String? = null) {
		navController.navigate(
			if (replyTo.isNullOrEmpty()) MainDestinations.COMPOSE_ROUTE else "${MainDestinations.COMPOSE_ROUTE}?replyTo=$replyTo"
		) {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}
			launchSingleTop = true
			restoreState = true
		}
	}

	fun navigateUp() {
		navController.navigateUp()
	}
}
