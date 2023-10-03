package sh.elizabeth.fedihome.ui.view.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sh.elizabeth.fedihome.ui.composable.PostFAB
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme
import sh.elizabeth.fedihome.ui.view.dashboard.DashboardDestinations.HOME
import sh.elizabeth.fedihome.ui.view.dashboard.DashboardDestinations.NOTIFICATIONS
import sh.elizabeth.fedihome.ui.view.dashboard.DashboardDestinations.SEARCH

@Composable
fun DashboardRoute(
	dashboardViewModel: DashboardViewModel = hiltViewModel(),
	windowSizeClass: WindowSizeClass,
	navToLogin: () -> Unit,
	navToCompose: (String?) -> Unit,
	navToPost: (String) -> Unit,
	navToProfile: (String) -> Unit,
) {
	val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

	if (uiState.isAuthLoading) {
		return
	}

	if (!uiState.isLoggedIn) {
		navToLogin()
		return
	}

	DashboardRoute(
		windowWidthSizeClass = windowSizeClass.widthSizeClass,
		navToCompose = navToCompose,
		navToPost = navToPost,
		navToProfile = navToProfile,
	)
}

@Composable
fun DashboardRoute(
	windowWidthSizeClass: WindowWidthSizeClass,
	navToCompose: (String?) -> Unit,
	navToPost: (String) -> Unit,
	navToProfile: (String) -> Unit,
) {
	var selectedTab by remember { mutableStateOf(HOME.route) }
	Scaffold(bottomBar = {
		if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
			DashboardNavBar(selectedTab) {
				selectedTab = it
			}
		}
	}, floatingActionButton = {
		PostFAB(onClick = { navToCompose(null) })
	}) { contentPadding ->
		Row(
			Modifier
				.fillMaxSize()
				.padding(contentPadding)
		) {
			if (windowWidthSizeClass != WindowWidthSizeClass.Compact) {
				DashboardNavRail(selectedTab) {
					selectedTab = it
				}
			}
			if (selectedTab == HOME.route) HomeScreen(
				navToCompose = navToCompose,
				navToPost = navToPost,
				navToProfile = navToProfile,
			)
			if (selectedTab == NOTIFICATIONS.route) Text("Notifications screen")
			if (selectedTab == SEARCH.route) Text("Search screen")

		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun DashboardRoutePreview() {
	FediHomeTheme {
		DashboardRoute(windowWidthSizeClass = WindowWidthSizeClass.Compact,
			navToCompose = {},
			navToPost = {},
			navToProfile = {})
	}
}
