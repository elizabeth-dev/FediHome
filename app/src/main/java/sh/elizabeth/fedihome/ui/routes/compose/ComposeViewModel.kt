package sh.elizabeth.fedihome.ui.routes.compose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.MainDestinations
import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.data.repository.PostRepository
import sh.elizabeth.fedihome.data.repository.ProfileRepository
import sh.elizabeth.fedihome.domain.CreatePostUseCase
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.PostDraft
import sh.elizabeth.fedihome.model.PostVisibility
import sh.elizabeth.fedihome.model.Profile
import javax.inject.Inject

data class ComposeUiState(
	val activeAccount: String? = null,
	val loggedInProfiles: List<Profile> = emptyList(),
	val isReply: Boolean = false,
	val replyTo: Post? = null,
)

@HiltViewModel
class ComposeViewModel @Inject constructor(
	private val createPostUseCase: CreatePostUseCase,
	private val authRepository: AuthRepository,
	private val postRepository: PostRepository,
	private val profileRepository: ProfileRepository,
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
	private val _uiState = MutableStateFlow(ComposeUiState())

	@OptIn(ExperimentalCoroutinesApi::class)
	val uiState = combine(
		authRepository.internalData.flatMapLatest { authData ->
			profileRepository.getMultipleByIdsFlow(authData.accounts.keys.toList())
				.map {
				Pair(authData.activeAccount, it)
			}

		},
		_uiState
	) { profilesData, uiState ->
		val replyTo =
			savedStateHandle.toRoute<MainDestinations.COMPOSE>().replyTo

		uiState.copy(
			activeAccount = profilesData.first,
			loggedInProfiles = profilesData.second,
			isReply = !replyTo.isNullOrBlank(),
			replyTo = replyTo?.let { postRepository.getPost(it) },
		)
	}.stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value)

	fun sendPost(text: String, contentWarning: String?) {
		viewModelScope.launch {
			authRepository.activeAccount.first().let {
				createPostUseCase(
					it, PostDraft(
						text = text,
						cw = if (contentWarning.isNullOrBlank()) null else contentWarning,
						visibility = PostVisibility.PUBLIC,
						visibleUserIds = emptyList(),
						localOnly = false,
						channelId = null,
						renoteId = null,
						replyId = null
					)
				)
			}
		}
	}

	fun switchActiveProfile(profileId: String, activeAccount: String?) {
		if (profileId == activeAccount) return

		viewModelScope.launch {
			authRepository.setActiveAccount(profileId)
		}
	}
}
