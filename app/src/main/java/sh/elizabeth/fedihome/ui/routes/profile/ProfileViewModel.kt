package sh.elizabeth.fedihome.ui.routes.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.MainDestinations
import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.data.repository.PostRepository
import sh.elizabeth.fedihome.data.repository.ProfileRepository
import sh.elizabeth.fedihome.domain.RefreshProfileAndPostsUseCase
import sh.elizabeth.fedihome.domain.VotePollUseCase
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.util.viewmodel.PostHandlingViewModel
import javax.inject.Inject

sealed interface ProfileUiState {
	val isLoading: Boolean
	val activeAccount: String
	val profileTag: String

	data class NoProfile(
		override val isLoading: Boolean,
		override val activeAccount: String,
		override val profileTag: String,
	) : ProfileUiState

	data class HasProfile(
		val profile: Profile,
		val posts: List<Post>?,
		override val isLoading: Boolean,
		override val activeAccount: String,
		override val profileTag: String,
	) : ProfileUiState
}

private data class ProfileViewModelState(
	val isLoading: Boolean = false,
	val profileTag: String,
) {
	fun toUiState(
		activeAccount: String = "",
		profile: Profile?,
		posts: List<Post>?,
	): ProfileUiState = if (profile == null) {
		ProfileUiState.NoProfile(
			isLoading = isLoading, activeAccount = activeAccount, profileTag = profileTag
		)
	}
	else {
		ProfileUiState.HasProfile(
			profile = profile,
			posts = posts,
			isLoading = isLoading,
			activeAccount = activeAccount,
			profileTag = profileTag
		)
	}
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
	override val votePollUseCase: VotePollUseCase,
	private val refreshProfileAndPostsUseCase: RefreshProfileAndPostsUseCase,
	private val profileRepository: ProfileRepository,
	override val postRepository: PostRepository,
	authRepository: AuthRepository,
	savedStateHandle: SavedStateHandle,
) : PostHandlingViewModel, ViewModel() {
	override val coroutineHandlingScope: CoroutineScope
		get() = viewModelScope

	private val profileTag = savedStateHandle.toRoute<MainDestinations.PROFILE>().profileTag

	private val viewModelState = MutableStateFlow(
		ProfileViewModelState(
			isLoading = true, profileTag = profileTag
		)
	)

	val uiState = combine(
		viewModelState,
		authRepository.activeAccount,
		profileRepository.getProfileByTagFlow(profileTag),
	) { state, activeAccount, profile ->
		val posts = profile.let {
			if (it == null) return@let null
			else postRepository.getPostsByProfileFlow(it.id).firstOrNull()
		}

		state.toUiState(activeAccount, profile, posts)
	}.stateIn(
		viewModelScope,
		SharingStarted.Eagerly,
		viewModelState.value.toUiState(profile = null, posts = null)
	)

	init {
		viewModelScope.launch {
			uiState.filter { it.activeAccount.isNotBlank() }.distinctUntilChangedBy {
				Triple(
					it.activeAccount,
					it.profileTag,
					(it as? ProfileUiState.HasProfile)?.profile?.id
				)
			}.collect {
				refreshProfile(
					it.activeAccount,
					it.profileTag,
					if (it is ProfileUiState.HasProfile) it.profile.id else null
				)
			}
		}
	}

	fun refreshProfile(activeAccount: String, profileTag: String, profileId: String?) {
		viewModelState.update {
			it.copy(isLoading = true)
		}
		viewModelScope.launch {
			if (profileId !== null) refreshProfileAndPostsUseCase(activeAccount, profileId)
			else profileRepository.fetchProfileByTag(activeAccount, profileTag)

			viewModelState.update {
				it.copy(isLoading = false)
			}
		}
	}
}
