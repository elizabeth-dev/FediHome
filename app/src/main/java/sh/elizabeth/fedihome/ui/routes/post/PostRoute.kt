package sh.elizabeth.fedihome.ui.routes.post

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sh.elizabeth.fedihome.mock.defaultPost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddBoost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddFavorite
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddReaction
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveBoost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveFavorite
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveReaction
import sh.elizabeth.fedihome.ui.compositionlocals.localOnVotePoll
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun PostRoute(
	postViewModel: PostViewModel = hiltViewModel(),
	navBack: () -> Unit,
) {
	val uiState by postViewModel.uiState.collectAsStateWithLifecycle()

	CompositionLocalProvider(localOnVotePoll provides { postId, pollId, choices ->
		postViewModel.votePoll(
			activeAccount = uiState.activeAccount,
			postId = postId,
			pollId = pollId,
			choices = choices
		)
	}, localOnAddFavorite provides {
		postViewModel.addFavorite(
			activeAccount = uiState.activeAccount, postId = it
		)
	}, localOnRemoveFavorite provides {
		postViewModel.removeFavorite(
			activeAccount = uiState.activeAccount, postId = it
		)
	}, localOnAddReaction provides { postId, reaction ->
		postViewModel.addReaction(
			activeAccount = uiState.activeAccount, postId = postId, reaction = reaction
		)
	}, localOnRemoveReaction provides { postId, reaction ->
		postViewModel.removeReaction(
			activeAccount = uiState.activeAccount, postId = postId, reaction = reaction
		)
	}, localOnAddBoost provides { postId ->
		postViewModel.addBoost(
			activeAccount = uiState.activeAccount, postId = postId
		)
	}, localOnRemoveBoost provides { postId ->
		postViewModel.removeBoost(
			activeAccount = uiState.activeAccount, postId = postId
		)
	}) {
		PostRoute(
			uiState = uiState,
			navBack = navBack,
			onPostRefresh = postViewModel::refreshPost,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostRoute(
	uiState: PostUiState,
	navBack: () -> Unit,
	onPostRefresh: (activeAccount: String, postId: String) -> Unit,
) {
	LaunchedEffect(key1 = uiState.activeAccount, key2 = uiState.postId) {
		if (uiState.activeAccount.isNotBlank()) onPostRefresh(
			uiState.activeAccount, uiState.postId
		)
	}

	Scaffold(topBar = {
		TopAppBar(title = {
			Text(
				text = "Post", maxLines = 1, overflow = TextOverflow.Ellipsis
			)
		}, navigationIcon = {
			IconButton(
				onClick = navBack
			) {
				Icon(
					Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back"
				)
			}
		})
	}) { contentPadding ->
		PostScreen(
			uiState = uiState,
			onPostRefresh = onPostRefresh,
			contentPadding = contentPadding,
		)

	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun PostRoutePreview() {
	FediHomeTheme {
		PostRoute(
			uiState = PostUiState.HasPost(
				postId = "foo", post = defaultPost, activeAccount = "foo", isLoading = false
			), navBack = {}, onPostRefresh = { _, _ -> })
	}
}
