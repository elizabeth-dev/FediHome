package sh.elizabeth.wastodon.ui.view.post

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun PostRoute(
	postViewModel: PostViewModel = hiltViewModel(),
	navBack: () -> Unit,
	navToCompose: (replyId: String) -> Unit,
) {
	val uiState by postViewModel.uiState.collectAsStateWithLifecycle()

	PostRoute(
		uiState = uiState,
		navBack = navBack,
		onPostRefresh = postViewModel::refreshPost,
		onReply = navToCompose,
		onVotePoll = postViewModel::votePoll
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostRoute(
	uiState: PostUiState,
	navBack: () -> Unit,
	onPostRefresh: (activeAccount: String, postId: String) -> Unit,
	onReply: (String) -> Unit,
	onVotePoll: (activeAccount: String, postId: String, choices: List<Int>) -> Unit,
) {
	Scaffold(topBar = {
		TopAppBar(title = { Text(text = "Post", maxLines = 1, overflow = TextOverflow.Ellipsis) },
			navigationIcon = {
				IconButton(
					onClick = navBack
				) {
					Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
				}
			})
	}) { contentPadding ->
		PostScreen(uiState = uiState,
			onPostRefresh = onPostRefresh,
			contentPadding = contentPadding,
			onReply = onReply,
			onVotePoll = { postId, choices -> onVotePoll(uiState.activeAccount, postId, choices) })

	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun PostRoutePreview() {
	WastodonTheme {
		PostRoute(uiState = PostUiState.HasPost(
			postId = "foo", post = Post(
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

						),
					quote = null,
					repostedBy = null,
					poll = Poll(
						voted = false, expiresAt = null, multiple = false, choices = listOf(
							PollChoice(
								text = "foo", votes = 0, isVoted = false
							), PollChoice(
								text = "bar", votes = 0, isVoted = false
							)
						)

					)

				),
				repostedBy = null,
				poll = Poll(
					voted = false, expiresAt = null, multiple = false, choices = listOf(
						PollChoice(
							text = "foo", votes = 0, isVoted = false
						), PollChoice(
							text = "bar", votes = 0, isVoted = false
						)
					)
				)
			), activeAccount = "foo", isLoading = false
		), navBack = {}, onPostRefresh = { _, _ -> }, onReply = {}, onVotePoll = { _, _, _ -> })
	}
}
