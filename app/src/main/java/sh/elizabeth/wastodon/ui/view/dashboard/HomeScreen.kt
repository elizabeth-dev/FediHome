package sh.elizabeth.wastodon.ui.view.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) {
	val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

	HomeScreen(uiState = uiState)
}

@Composable
fun HomeScreen(uiState: HomeUiState) {
	Column(
		Modifier.wrapContentSize(Alignment.Center),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		if (uiState.isLoading) {
			CircularProgressIndicator()
			return
		}
		when (uiState) {
			is HomeUiState.NoPosts -> Text("No posts")
			is HomeUiState.HasPosts -> Text("Has posts")
		}
	}
}
