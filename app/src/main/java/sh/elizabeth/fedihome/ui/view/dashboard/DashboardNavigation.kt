package sh.elizabeth.fedihome.ui.view.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
	currentProfileName: String,
	onAccountPickerShow: () -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {

	CenterAlignedTopAppBar(
		title = {
			Text(
				text = currentProfileName, maxLines = 1, overflow = TextOverflow.Ellipsis
			)
		},
		navigationIcon = {
			IconButton(onClick = onAccountPickerShow) {
				Icon(
					imageVector = Icons.Rounded.AccountCircle,
					contentDescription = "Account picker"
				)
			}
		},
		scrollBehavior = scrollBehavior
	)
}

@Composable
fun DashboardNavBar(selectedTab: String, onTabSelected: (String) -> Unit) {
	NavigationBar {
		dashboardDestinations.forEach { destination ->
			NavigationBarItem(icon = {
				Icon(
					if (destination.route == selectedTab) destination.selectedIcon else destination.unselectedIcon,
					contentDescription = destination.iconTextId
				)
			},
				selected = destination.route == selectedTab,
				onClick = { onTabSelected(destination.route) },
				label = { Text(destination.iconTextId) })
		}
	}
}

@Composable
fun DashboardNavRail(
	selectedTab: String,
	onTabSelected: (String) -> Unit,
	onAccountPickerShow: () -> Unit,
) {
	NavigationRail(header = {
		IconButton(onClick = onAccountPickerShow) {
			Icon(
				imageVector = Icons.Rounded.AccountCircle,
				contentDescription = "Account picker"
			)
		}
	}) {
		Spacer(Modifier.weight(1f))
		dashboardDestinations.forEach { destination ->
			NavigationRailItem(icon = {
				Icon(
					if (destination.route == selectedTab) destination.selectedIcon else destination.unselectedIcon,
					contentDescription = destination.iconTextId
				)
			},
				selected = destination.route == selectedTab,
				onClick = { onTabSelected(destination.route) },
				label = { Text(destination.iconTextId) })
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
