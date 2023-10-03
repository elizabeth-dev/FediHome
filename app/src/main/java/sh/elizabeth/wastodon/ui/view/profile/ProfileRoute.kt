package sh.elizabeth.wastodon.ui.view.profile

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sh.elizabeth.wastodon.model.Poll
import sh.elizabeth.wastodon.model.PollChoice
import sh.elizabeth.wastodon.model.Post
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.ui.theme.WastodonTheme
import java.time.Instant

@Composable
fun ProfileRoute(
	profileViewModel: ProfileViewModel = hiltViewModel(),
	navBack: () -> Unit,
	navToCompose: (postId: String) -> Unit,
	navToPost: (postId: String) -> Unit,
	navToProfile: (profileId: String) -> Unit,
) {
	val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

	ProfileRoute(
		uiState = uiState,
		navBack = navBack,
		onRefresh = profileViewModel::refreshProfile,
		onReply = navToCompose,
		onVotePoll = profileViewModel::votePoll,
		navToPost = navToPost,
		navToProfile = navToProfile
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRoute(
	uiState: ProfileUiState,
	navBack: () -> Unit,
	onRefresh: (activeAccount: String, postId: String) -> Unit,
	onReply: (String) -> Unit,
	onVotePoll: (activeAccount: String, postId: String, pollId: String?, List<Int>) -> Unit,
	navToPost: (String) -> Unit,
	navToProfile: (String) -> Unit,
) {
	Scaffold(topBar = {
		TopAppBar(
			title = { Text(text = "", maxLines = 1, overflow = TextOverflow.Ellipsis) },
			navigationIcon = {
				IconButton(
					onClick = navBack
				) {
					Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
				}
			},
			colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
		)
	}) { contentPadding ->
		ProfileScreen(
			uiState = uiState,
			onRefresh = onRefresh,
			contentPadding = contentPadding,
			onReply = onReply,
			onVotePoll = onVotePoll,
			navToPost = navToPost,
			navToProfile = navToProfile
		)
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun ProfileRoutePreview() {
	WastodonTheme {
		ProfileRoute(uiState = ProfileUiState.HasProfile(
			profileId = "foo", profile = Profile(
				id = "foo",
				username = "elizabeth",
				name = "Elizabeth",
				avatarUrl = null,
				avatarBlur = null,
				instance = "blahaj.zone",
				fullUsername = "elizabeth@blahaj.zone",
				headerUrl = null,
				headerBlur = null,
				following = null,
				followers = null,
				postCount = null,
				createdAt = null,
				fields = emptyList(),
				description = "Lorem Ipsum Dolor Sit Amet",
				emojis = emptyMap(),
			), posts = listOf(
				Post(
					id = "foo",
					createdAt = Instant.now(),
					updatedAt = null,
					cw = "foo",
					text = "bar",
					author = Profile(
						id = "foo",
						username = "elizabeth",
						name = "Elizabeth",
						avatarUrl = null,
						avatarBlur = null,
						instance = "blahaj.zone",
						fullUsername = "elizabeth@blahaj.zone",
						headerUrl = null,
						headerBlur = null,
						following = null,
						followers = null,
						postCount = null,
						createdAt = null,
						fields = emptyList(),
						description = "Lorem Ipsum Dolor Sit Amet",
						emojis = emptyMap(),
					),
					quote = Post(
						id = "foo",
						createdAt = Instant.now(),
						updatedAt = null,
						cw = null,
						text = "bar",
						author = Profile(
							id = "foo",
							username = "elizabeth",
							name = "Elizabeth",
							avatarUrl = null,
							avatarBlur = null,
							instance = "blahaj.zone",
							fullUsername = "elizabeth@blahaj.zone",
							headerUrl = null,
							headerBlur = null,
							following = null,
							followers = null,
							postCount = null,
							createdAt = null,
							fields = emptyList(),
							description = "Lorem Ipsum Dolor Sit Amet",
							emojis = emptyMap(),
						),
						quote = null,
						repostedBy = null,
						poll = Poll(
							id = null,
							voted = false,
							expiresAt = null,
							multiple = false,
							choices = listOf(
								PollChoice(
									text = "foo", votes = 0, isVoted = false
								), PollChoice(
									text = "bar", votes = 0, isVoted = false
								)
							)

						),
						emojis = emptyMap(),
					),
					repostedBy = null,
					poll = Poll(
						id = null,
						voted = false,
						expiresAt = null,
						multiple = false,
						choices = listOf(
							PollChoice(
								text = "foo", votes = 0, isVoted = false
							), PollChoice(
								text = "bar", votes = 0, isVoted = false
							)
						)
					),
					emojis = emptyMap(),
				)
			), activeAccount = "foo", isLoading = false
		),
			navBack = {},
			onRefresh = { _, _ -> },
			onReply = {},
			onVotePoll = { _, _, _, _ -> },
			navToPost = {},
			navToProfile = {})
	}
}
