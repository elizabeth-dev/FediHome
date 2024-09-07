package sh.elizabeth.fedihome.ui.routes.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.MainDestinations
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
	val isLoading: Boolean = false,
	val profileId: String,
) {
	fun toUiState(
		activeAccount: String = "",
		profile: Profile?,
		posts: List<Post>?,
	): ProfileUiState = if (profile == null) {
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
	profileRepository: ProfileRepository,
	postRepository: PostRepository,
	authRepository: AuthRepository,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {
	private val profileId =
		savedStateHandle.toRoute<MainDestinations.PROFILE>().profileId

	private val viewModelState = MutableStateFlow(
		ProfileViewModelState(
			isLoading = true,
			profileId = profileId,
		)
	)

	val uiState = combine(
		viewModelState,
		authRepository.activeAccount,
		profileRepository.getProfileFlow(profileId),
		postRepository.getPostsByProfileFlow(profileId)
	) { state, activeAccount, profile, posts ->
		state.toUiState(activeAccount, profile, posts)
	}.stateIn(
		viewModelScope,
		SharingStarted.Eagerly,
		viewModelState.value.toUiState(profile = null, posts = null)
	)

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
