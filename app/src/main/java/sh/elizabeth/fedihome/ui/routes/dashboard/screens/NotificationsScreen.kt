package sh.elizabeth.fedihome.ui.routes.dashboard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.ui.composable.NotificationCard

@Composable
fun NotificationsScreen(
	notificationsViewModel: NotificationsViewModel = hiltViewModel(),
	navToCompose: (String) -> Unit,
	navToPost: (String) -> Unit,
	navToProfile: (String) -> Unit,
) {
	val uiState by notificationsViewModel.uiState.collectAsStateWithLifecycle()

	NotificationsScreen(
		uiState = uiState,
		onRefresh = notificationsViewModel::refreshNotifications,
		onReply = navToCompose,
		onVotePoll = notificationsViewModel::votePoll,
		navToPost = navToPost,
		navToProfile = navToProfile
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
	uiState: NotificationsUiState,
	onRefresh: (profileId: String) -> Unit,
	navToPost: (String) -> Unit,
	navToProfile: (String) -> Unit,
	onReply: (String) -> Unit,
	onVotePoll: (activeAccount: String, postId: String, pollId: String?, List<Int>) -> Unit,
) {
	val pullRefreshState = rememberPullToRefreshState()
	var isRefreshing by remember { mutableStateOf(false) }

	val coroutineScope = rememberCoroutineScope()
	val launchOnRefresh: () -> Unit = {
		isRefreshing = true
		coroutineScope.launch {
			if (uiState.activeAccount.isNotBlank()) onRefresh(uiState.activeAccount)
			isRefreshing = false
		}
	}

	LaunchedEffect(key1 = uiState.activeAccount) {
		launchOnRefresh()
	}

	PullToRefreshBox(
		modifier = Modifier.fillMaxSize(),
		state = pullRefreshState,
		isRefreshing = isRefreshing,
		onRefresh = launchOnRefresh
	) {
		when (uiState) {
			is NotificationsUiState.NoNotifications -> if (!uiState.isLoading) Column(
				Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(text = "No notifications yet!")
			}

			is NotificationsUiState.HasNotifications -> {
				LazyColumn(Modifier.fillMaxSize()) {
					items(uiState.notifications) { notification ->
						NotificationCard(notification = notification,
							navToProfile = navToProfile,
							navToPost = navToPost,
							onReply = onReply,
							onVotePoll = {
								onVotePoll(
									uiState.activeAccount,
									notification.post?.id!!,
									notification.post.poll?.id,
									it
								)
							})
					}
				}
			}
		}
	}
}
