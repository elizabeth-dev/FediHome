package sh.elizabeth.wastodon.ui.view.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import sh.elizabeth.wastodon.ui.composable.SlimPostCard

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostScreen(
	uiState: PostUiState,
	contentPadding: PaddingValues,
	onPostRefresh: (activeAccount: String, postId: String) -> Unit,
	onReply: (String) -> Unit,
	onVotePoll: (postId: String, choices: List<Int>) -> Unit,
) {
	val pullRefreshState = rememberPullRefreshState(
		uiState.isLoading,
		{ onPostRefresh(uiState.activeAccount, uiState.postId) })

	Box(
		modifier = Modifier
			.fillMaxSize()
			.padding(contentPadding)
			.pullRefresh(pullRefreshState)
	) {
		Divider(thickness = 1.dp, modifier = Modifier.zIndex(1f))

		when (uiState) {
			is PostUiState.NoPost -> if (!uiState.isLoading) Column(
				Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(text = "Post not found")
			}

			is PostUiState.HasPost -> Column(
				Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
			) {
				Surface {
					SlimPostCard(
						post = uiState.post,
						onReply = onReply,
						onVotePoll = onVotePoll,
						navToPost = {}
					)
				}
			}
		}

		PullRefreshIndicator(
			uiState.isLoading, pullRefreshState, Modifier.align(Alignment.TopCenter)
		)
	}
}
