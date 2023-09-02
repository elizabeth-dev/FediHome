package sh.elizabeth.wastodon.ui.view.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sh.elizabeth.wastodon.ui.composable.PostFAB
import sh.elizabeth.wastodon.ui.theme.WastodonTheme
import sh.elizabeth.wastodon.ui.view.dashboard.DashboardDestinations.*

@Composable
fun DashboardRoute(
	dashboardViewModel: DashboardViewModel = hiltViewModel(),
	windowSizeClass: WindowSizeClass,
	navToLogin: () -> Unit,
) {
	val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

	if (uiState.isAuthLoading) {
		return
	}

	if (!uiState.isLoggedIn) {
		navToLogin()
		return
	}

	DashboardRoute(windowWidthSizeClass = windowSizeClass.widthSizeClass)
}

@Composable
fun DashboardRoute(windowWidthSizeClass: WindowWidthSizeClass) {
	var selectedTab by remember { mutableStateOf(HOME.route) }
	Scaffold(bottomBar = {
		if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
			DashboardNavBar(selectedTab) {
				selectedTab = it
			}
		}
	}, floatingActionButton = {
		PostFAB { }
	}) { contentPadding ->
		Row(Modifier.fillMaxSize().padding(contentPadding)) {
			if (windowWidthSizeClass != WindowWidthSizeClass.Compact) {
				DashboardNavRail(selectedTab) {
					selectedTab = it
				}
			}
			if (selectedTab == HOME.route) HomeScreen()
			if (selectedTab == NOTIFICATIONS.route) Text("Notifications screen")
			if (selectedTab == SEARCH.route) Text("Search screen")

		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun DashboardRoutePreview() {
	WastodonTheme { DashboardRoute(WindowWidthSizeClass.Compact) }
}
