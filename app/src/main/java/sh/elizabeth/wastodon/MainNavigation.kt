package sh.elizabeth.wastodon

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object MainDestinations {
	const val DASHBOARD_ROUTE = "dashboard"
	const val LOGIN_ROUTE = "login"
}

class MainNavigationActions(navController: NavHostController) {
	val navigateToDashboard: () -> Unit = {
		navController.navigate(MainDestinations.DASHBOARD_ROUTE) {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = false // This is needed for first login flow
			}
			launchSingleTop = true
			restoreState = true
		}
	}
	val navigateToLogin: () -> Unit = {
		navController.navigate(MainDestinations.LOGIN_ROUTE) {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}
			launchSingleTop = true
			restoreState = true
		}
	}
}
