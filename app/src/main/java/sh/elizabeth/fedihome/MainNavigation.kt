package sh.elizabeth.fedihome

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

object MainDestinations {
	@Serializable
	object DASHBOARD
	@Serializable
	object LOGIN_OAUTH
	@Serializable
	object LOGIN_NOTIFICATIONS
	@Serializable
	data class COMPOSE(val replyTo: String? = null)
	@Serializable
	data class POST(val postId: String)
	@Serializable
	data class PROFILE(val profileId: String)
}

class MainNavigationActions(private val navController: NavHostController) {
	fun navigateToDashboard() {
		navController.navigate(MainDestinations.DASHBOARD) {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = false // This is needed for first login flow
			}
			launchSingleTop = true
			restoreState = true
		}
	}

	fun navigateToLoginOAuth() {
		navController.navigate(MainDestinations.LOGIN_OAUTH) {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}
			launchSingleTop = true
			restoreState = true
		}
	}

	fun navigateToCompose(replyTo: String? = null) {
		navController.navigate(MainDestinations.COMPOSE(replyTo)) {
			launchSingleTop = true
			restoreState = true
		}
	}

	fun navigateToPost(postId: String) {
		navController.navigate(
			MainDestinations.POST(postId)
		) {
			launchSingleTop = false
			restoreState = true
		}
	}

	fun navigateToProfile(profileId: String) {
		navController.navigate(
			MainDestinations.PROFILE(profileId)
		) {
			launchSingleTop = false
			restoreState = true
		}
	}

	fun navigateUp() {
		navController.navigateUp()
	}
}
