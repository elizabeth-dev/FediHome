package sh.elizabeth.fedihome.ui.routes.dashboard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sh.elizabeth.fedihome.ui.composable.NotificationCard
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddBoost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddFavorite
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddReaction
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveBoost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveFavorite
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveReaction
import sh.elizabeth.fedihome.ui.compositionlocals.localOnVotePoll

@Composable
fun NotificationsScreen(
	notificationsViewModel: NotificationsViewModel = hiltViewModel(),
	scrollState: LazyListState,
) {
	val uiState by notificationsViewModel.uiState.collectAsStateWithLifecycle()

	CompositionLocalProvider(localOnVotePoll provides { postId, pollId, choices ->
		notificationsViewModel.votePoll(
			activeAccount = uiState.activeAccount,
			postId = postId,
			pollId = pollId,
			choices = choices
		)
	}, localOnAddFavorite provides {
		notificationsViewModel.addFavorite(
			activeAccount = uiState.activeAccount, postId = it
		)
	}, localOnRemoveFavorite provides {
		notificationsViewModel.removeFavorite(
			activeAccount = uiState.activeAccount, postId = it
		)
	}, localOnAddReaction provides { postId, reaction ->
		notificationsViewModel.addReaction(
			activeAccount = uiState.activeAccount, postId = postId, reaction = reaction
		)
	}, localOnRemoveReaction provides { postId, reaction ->
		notificationsViewModel.removeReaction(
			activeAccount = uiState.activeAccount, postId = postId, reaction = reaction
		)
	}, localOnAddBoost provides { postId ->
		notificationsViewModel.addBoost(
			activeAccount = uiState.activeAccount, postId = postId
		)
	}, localOnRemoveBoost provides { postId ->
		notificationsViewModel.removeBoost(
			activeAccount = uiState.activeAccount, postId = postId
		)
	}) {
		NotificationsScreen(
			uiState = uiState,
			scrollState = scrollState,
			onRefresh = notificationsViewModel::refreshNotifications,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NotificationsScreen(
	uiState: NotificationsUiState,
	scrollState: LazyListState,
	onRefresh: (profileId: String) -> Unit,
) {
	val pullRefreshState =
		rememberPullRefreshState(uiState.isLoading, { onRefresh(uiState.activeAccount) })

	LaunchedEffect(key1 = uiState.activeAccount) {
		if (uiState.activeAccount.isNotBlank()) onRefresh(uiState.activeAccount)
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.pullRefresh(pullRefreshState, true)
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
				LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState) {
					items(uiState.notifications) { notification ->
						NotificationCard(
							notification = notification,
						)
					}
				}
			}
		}

		PullRefreshIndicator(
			uiState.isLoading, pullRefreshState, Modifier.align(Alignment.TopCenter)
		)
	}
}
