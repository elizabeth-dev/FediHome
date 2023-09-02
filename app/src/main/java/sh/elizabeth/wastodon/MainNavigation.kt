package sh.elizabeth.wastodon

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object MainDestinations {
	const val HOME_ROUTE = "home"
	const val LOGIN_ROUTE = "login"
}

class MainNavigationActions(navController: NavHostController) {
	val navigateToHome: () -> Unit = {
		navController.navigate(MainDestinations.HOME_ROUTE) {
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
