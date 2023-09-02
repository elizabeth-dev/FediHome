package sh.elizabeth.wastodon.ui.view.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sh.elizabeth.wastodon.ui.composable.SlimPostCard

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) {
	val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

	HomeScreen(uiState = uiState, onRefresh = { homeViewModel.refreshTimeline(it) })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(uiState: HomeUiState, onRefresh: (String) -> Unit) {
	val pullRefreshState =
		rememberPullRefreshState(uiState.isLoading, { onRefresh(uiState.activeAccount) })
	Box(
		Modifier
			.fillMaxSize()
			.pullRefresh(pullRefreshState, true)

	) {
		when (uiState) {
			is HomeUiState.NoPosts -> if (!uiState.isLoading) Column(
				Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(text = "No posts yet!")
			}

			is HomeUiState.HasPosts -> LazyColumn(Modifier.fillMaxSize()) {
				items(uiState.posts) { post ->
					SlimPostCard(post = post)
				}
			}
		}

		PullRefreshIndicator(
			uiState.isLoading,
			pullRefreshState,
			Modifier.align(Alignment.TopCenter)
		)
	}
}
