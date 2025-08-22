package sh.elizabeth.fedihome.ui.routes.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import sh.elizabeth.fedihome.localNavToCompose
import sh.elizabeth.fedihome.mock.defaultProfile
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.ui.composable.AccountPicker
import sh.elizabeth.fedihome.ui.composable.PostFAB
import sh.elizabeth.fedihome.ui.routes.dashboard.DashboardDestinations.HOME
import sh.elizabeth.fedihome.ui.routes.dashboard.DashboardDestinations.NOTIFICATIONS
import sh.elizabeth.fedihome.ui.routes.dashboard.DashboardDestinations.SEARCH
import sh.elizabeth.fedihome.ui.routes.dashboard.screens.HomeScreen
import sh.elizabeth.fedihome.ui.routes.dashboard.screens.NotificationsScreen
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun DashboardRoute(
	dashboardViewModel: DashboardViewModel = hiltViewModel(),
	windowSizeClass: WindowSizeClass,
	navToLogin: () -> Unit,
) {
	val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

	when (uiState) {
		is DashboardUiState.Loading -> return
		is DashboardUiState.NotLoggedIn -> {
			navToLogin()
			return
		}

		is DashboardUiState.LoggedIn -> DashboardRoute(
			windowWidthSizeClass = windowSizeClass.widthSizeClass,
			loggedInProfiles = (uiState as DashboardUiState.LoggedIn).loggedInProfiles,
			activeAccount = (uiState as DashboardUiState.LoggedIn).activeAccount,
			navToAddAccount = navToLogin,
			switchActiveProfile = {
				dashboardViewModel.switchActiveProfile(
					it, (uiState as DashboardUiState.LoggedIn).activeAccount
				)
			})
	}

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardRoute(
	windowWidthSizeClass: WindowWidthSizeClass,
	loggedInProfiles: List<Profile>,
	activeAccount: String,
	navToAddAccount: () -> Unit,
	switchActiveProfile: (profileId: String) -> Unit,
) {
	val navToCompose = localNavToCompose.current
	var selectedTab by remember { mutableStateOf(HOME.route) }
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
	var showAccountPicker by remember { mutableStateOf(false) }

	// FIXME: activeAccount might not be cached? cache on startup?
	val activeProfile = loggedInProfiles.find { it.id == activeAccount }!!

	Scaffold(bottomBar = {
		if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
			DashboardNavBar(selectedTab) {
				selectedTab = it
			}
		}
	}, topBar = {
		if (windowWidthSizeClass == WindowWidthSizeClass.Compact) DashboardTopBar(
			currentProfileName = "@${activeProfile.username}",
			onAccountPickerShow = { showAccountPicker = true },
			scrollBehavior = scrollBehavior
		)
	}, floatingActionButton = {
		PostFAB(onClick = { navToCompose(null) })
	}) { contentPadding ->
		Row(
			modifier = Modifier
				.fillMaxSize()
				.padding(contentPadding)
				.consumeWindowInsets(contentPadding),
		) {
			if (windowWidthSizeClass != WindowWidthSizeClass.Compact) {
				DashboardNavRail(selectedTab = selectedTab, onTabSelected = {
					selectedTab = it
				}, onAccountPickerShow = { showAccountPicker = true })
			}
			if (selectedTab == HOME.route) HomeScreen()
			if (selectedTab == NOTIFICATIONS.route) NotificationsScreen()
			if (selectedTab == SEARCH.route) Text("Search screen")

			AccountPicker(
				isVisible = showAccountPicker,
				profiles = loggedInProfiles,
				activeProfileId = activeAccount,
				canAddProfile = true,
				onSwitch = switchActiveProfile,
				onAddProfile = navToAddAccount,
				onDismiss = { showAccountPicker = false })

		}

	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun DashboardRoutePreview() {
	FediHomeTheme {
		DashboardRoute(
			windowWidthSizeClass = WindowWidthSizeClass.Compact,
			loggedInProfiles = listOf(defaultProfile, defaultProfile),
			switchActiveProfile = {},
			activeAccount = "foo",
			navToAddAccount = {})
	}
}
