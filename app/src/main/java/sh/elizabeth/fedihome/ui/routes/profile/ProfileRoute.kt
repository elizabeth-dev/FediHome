package sh.elizabeth.fedihome.ui.routes.profile

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sh.elizabeth.fedihome.mock.defaultPost
import sh.elizabeth.fedihome.mock.defaultProfile
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddBoost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddFavorite
import sh.elizabeth.fedihome.ui.compositionlocals.localOnAddReaction
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveBoost
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveFavorite
import sh.elizabeth.fedihome.ui.compositionlocals.localOnRemoveReaction
import sh.elizabeth.fedihome.ui.compositionlocals.localOnVotePoll
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

@Composable
fun ProfileRoute(
	profileViewModel: ProfileViewModel = hiltViewModel(),
	navBack: () -> Unit,
) {
	val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

	CompositionLocalProvider(localOnVotePoll provides { postId, pollId, choices ->
		profileViewModel.votePoll(
			activeAccount = uiState.activeAccount,
			postId = postId,
			pollId = pollId,
			choices = choices
		)
	}, localOnAddFavorite provides {
		profileViewModel.addFavorite(
			activeAccount = uiState.activeAccount, postId = it
		)
	}, localOnRemoveFavorite provides {
		profileViewModel.removeFavorite(
			activeAccount = uiState.activeAccount, postId = it
		)
	}, localOnAddReaction provides { postId, reaction ->
		profileViewModel.addReaction(
			activeAccount = uiState.activeAccount, postId = postId, reaction = reaction
		)
	}, localOnRemoveReaction provides { postId, reaction ->
		profileViewModel.removeReaction(
			activeAccount = uiState.activeAccount, postId = postId, reaction = reaction
		)
	}, localOnAddBoost provides { postId ->
		profileViewModel.addBoost(
			activeAccount = uiState.activeAccount, postId = postId
		)
	}, localOnRemoveBoost provides { postId ->
		profileViewModel.removeBoost(
			activeAccount = uiState.activeAccount, postId = postId
		)
	}) {
		ProfileRoute(
			uiState = uiState,
			navBack = navBack,
			onRefresh = profileViewModel::refreshProfile,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRoute(
	uiState: ProfileUiState,
	navBack: () -> Unit,
	onRefresh: (activeAccount: String, profileTag: String, profileId: String?) -> Unit,
) {
	Scaffold(topBar = {
		TopAppBar(
			title = {
				Text(
					text = "", maxLines = 1, overflow = TextOverflow.Ellipsis
				)
			}, navigationIcon = {
				IconButton(
					onClick = navBack
				) {
					Icon(
						Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back"
					)
				}
			}, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
		)
	}) { contentPadding ->
		ProfileScreen(
			uiState = uiState,
			onRefresh = onRefresh,
			contentPadding = contentPadding,
		)
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun ProfileRoutePreview() {
	FediHomeTheme {
		ProfileRoute(
			uiState = ProfileUiState.HasProfile(
				profileTag = "foo", profile = defaultProfile, posts = listOf(
					defaultPost
				), activeAccount = "foo", isLoading = false
			),
			navBack = {},
			onRefresh = { _, _, _ -> },
		)
	}
}
