package sh.elizabeth.fedihome.ui.view.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

enum class DashboardDestinations(
	val selectedIcon: ImageVector,
	val unselectedIcon: ImageVector,
	val iconTextId: String,
	val titleTextId: String,
	val route: String,
) {
	HOME(
		selectedIcon = Icons.Rounded.Home,
		unselectedIcon = Icons.Outlined.Home,
		iconTextId = "Home",
		titleTextId = "Home",
		route = "home",
	),
	NOTIFICATIONS(
		selectedIcon = Icons.Rounded.Notifications,
		unselectedIcon = Icons.Outlined.Notifications,
		iconTextId = "Notifications",
		titleTextId = "Notifications",
		route = "notifications",
	),
	SEARCH(
		selectedIcon = Icons.Rounded.Search,
		unselectedIcon = Icons.Outlined.Search,
		iconTextId = "Search",
		titleTextId = "Search",
		route = "search",
	),
}

private val dashboardDestinations = DashboardDestinations.entries

@Composable
fun DashboardNavBar(selectedTab: String, onTabSelected: (String) -> Unit) {
	NavigationBar {
		dashboardDestinations.forEach { destination ->
			NavigationBarItem(
				icon = {
					Icon(
						if (destination.route == selectedTab) destination.selectedIcon else destination.unselectedIcon,
						contentDescription = destination.iconTextId
					)
				},
				selected = destination.route == selectedTab,
				onClick = { onTabSelected(destination.route) },
				label = { Text(destination.iconTextId) }
			)
		}
	}
}

@Composable
fun DashboardNavRail(selectedTab: String, onTabSelected: (String) -> Unit) {
	NavigationRail {
		Spacer(Modifier.weight(1f))
		dashboardDestinations.forEach { destination ->
			NavigationRailItem(
				icon = {
					Icon(
						if (destination.route == selectedTab) destination.selectedIcon else destination.unselectedIcon,
						contentDescription = destination.iconTextId
					)
				},
				selected = destination.route == selectedTab,
				onClick = { onTabSelected(destination.route) },
				label = { Text(destination.iconTextId) }
			)
		}
		Spacer(Modifier.weight(1f))

	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun DashboardNavBarPreview() {
	DashboardNavBar(DashboardDestinations.HOME.route) {}
}
