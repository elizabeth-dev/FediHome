package sh.elizabeth.fedihome.ui.routes.dashboard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import sh.elizabeth.fedihome.ui.composable.NotificationCard
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddBoost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddFavorite
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddReaction
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveBoost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveFavorite
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveReaction
import sh.elizabeth.fedihome.ui.compositionlocals.localOnVotePoll

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationsScreen(
	notificationsViewModel: NotificationsViewModel = hiltViewModel(),
	scrollState: LazyListState,
) {
	val activeAccount by notificationsViewModel.activeAccount.collectAsStateWithLifecycle()
	val lazyPagingItems = notificationsViewModel.pagingFlow.collectAsLazyPagingItems()

	val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading

	CompositionLocalProvider(localOnVotePoll provides { postId, pollId, choices ->
		notificationsViewModel.votePoll(
			activeAccount = activeAccount,
			postId = postId,
			pollId = pollId,
			choices = choices
		)
	}, localOnAddFavorite provides {
		notificationsViewModel.addFavorite(
			activeAccount = activeAccount, postId = it
		)
	}, localOnRemoveFavorite provides {
		notificationsViewModel.removeFavorite(
			activeAccount = activeAccount, postId = it
		)
	}, localOnAddReaction provides { postId, reaction ->
		notificationsViewModel.addReaction(
			activeAccount = activeAccount, postId = postId, reaction = reaction
		)
	}, localOnRemoveReaction provides { postId, reaction ->
		notificationsViewModel.removeReaction(
			activeAccount = activeAccount, postId = postId, reaction = reaction
		)
	}, localOnAddBoost provides { postId ->
		notificationsViewModel.addBoost(
			activeAccount = activeAccount, postId = postId
		)
	}, localOnRemoveBoost provides { postId ->
		notificationsViewModel.removeBoost(
			activeAccount = activeAccount, postId = postId
		)
	}) {
		val pullRefreshState =
			rememberPullRefreshState(isRefreshing, { lazyPagingItems.refresh() })

		Box(
			modifier = Modifier
				.fillMaxSize()
				.pullRefresh(pullRefreshState, true)
		) {
			if (lazyPagingItems.itemCount == 0 && !isRefreshing) {
				Column(
					Modifier
						.fillMaxSize()
						.verticalScroll(rememberScrollState()),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(text = "No notifications yet!")
				}
			}
			else {
				LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState) {
					items(lazyPagingItems.itemCount) { index ->
						lazyPagingItems[index]?.let { notification ->
							NotificationCard(notification = notification)
						}
					}
				}
			}

			PullRefreshIndicator(
				isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter)
			)
		}
	}
}
