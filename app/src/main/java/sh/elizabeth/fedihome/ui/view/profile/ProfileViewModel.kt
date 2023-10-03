package sh.elizabeth.fedihome.ui.view.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.data.repository.PostRepository
import sh.elizabeth.fedihome.data.repository.ProfileRepository
import sh.elizabeth.fedihome.domain.RefreshProfileAndPostsUseCase
import sh.elizabeth.fedihome.domain.VotePollUseCase
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.Profile
import javax.inject.Inject

sealed interface ProfileUiState {
	val isLoading: Boolean
	val activeAccount: String
	val profileId: String

	data class NoProfile(
		override val isLoading: Boolean,
		override val activeAccount: String,
		override val profileId: String,
	) : ProfileUiState

	data class HasProfile(
		val profile: Profile,
		val posts: List<Post>?,
		override val isLoading: Boolean,
		override val activeAccount: String,
		override val profileId: String,
	) : ProfileUiState
}

private data class ProfileViewModelState(
	val profile: Profile? = null,
	val posts: List<Post>? = null,
	val isLoading: Boolean = false,
	val activeAccount: String = "",
	val profileId: String,
) {
	fun toUiState(): ProfileUiState = if (profile == null) {
		ProfileUiState.NoProfile(
			isLoading = isLoading, activeAccount = activeAccount, profileId = profileId
		)
	} else {
		ProfileUiState.HasProfile(
			profile = profile,
			posts = posts,
			isLoading = isLoading,
			activeAccount = activeAccount,
			profileId = profileId
		)
	}
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
	private val votePollUseCase: VotePollUseCase,
	private val refreshProfileAndPostsUseCase: RefreshProfileAndPostsUseCase,
	private val profileRepository: ProfileRepository,
	private val postRepository: PostRepository,
	authRepository: AuthRepository,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {
	private val profileId = savedStateHandle.get<String>("profileId")
		?: throw IllegalArgumentException("ProfileID is required")

	private val viewModelState = MutableStateFlow(
		ProfileViewModelState(
			isLoading = true,
			profileId = profileId,
		)
	)

	val uiState = viewModelState.map(ProfileViewModelState::toUiState)
		.stateIn(viewModelScope, SharingStarted.Lazily, viewModelState.value.toUiState())

	init {
		viewModelScope.launch {
			authRepository.activeAccount.collect { activeAccount ->
				refreshProfile(activeAccount, profileId)
				viewModelState.update {
					it.copy(activeAccount = activeAccount)
				}
			}
		}

		viewModelScope.launch {
			postRepository.getPostsByProfileFlow(profileId).collect { posts ->
				viewModelState.update {
					it.copy(posts = posts)
				}
			}
		}

		viewModelScope.launch {
			profileRepository.getProfileFlow(profileId).collect { profile ->
				viewModelState.update {
					it.copy(profile = profile)
				}
			}
		}
	}

	fun refreshProfile(activeAccount: String, profileId: String) {
		viewModelState.update {
			it.copy(isLoading = true)
		}
		viewModelScope.launch {
			refreshProfileAndPostsUseCase(activeAccount, profileId)
			viewModelState.update {
				it.copy(isLoading = false)
			}
		}
	}

	fun votePoll(activeAccount: String, postId: String, pollId: String?, choices: List<Int>) {
		viewModelScope.launch {
			votePollUseCase(activeAccount, postId, pollId, choices)
		}
	}
}
