package sh.elizabeth.wastodon.ui.view.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
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
	val pullrefreshState = rememberPullRefreshState(uiState.isLoading, { onRefresh(uiState.activeAccount) })
	Column(
		Modifier.wrapContentSize(Alignment.Center),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		when (uiState) {
			is HomeUiState.NoPosts -> Button(onClick = { onRefresh(uiState.activeAccount) }) {
				Text("Refresh")
			}

			is HomeUiState.HasPosts -> Box(Modifier.pullRefresh(pullrefreshState)) {
				LazyColumn(Modifier.fillMaxSize()) {
					items(uiState.posts) { post ->
						SlimPostCard(post = post)
					}
				}
				PullRefreshIndicator(uiState.isLoading, pullrefreshState, Modifier.align(Alignment.TopCenter))
			}
		}
	}
}
