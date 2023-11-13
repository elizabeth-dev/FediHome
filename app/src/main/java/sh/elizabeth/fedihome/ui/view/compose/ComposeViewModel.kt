package sh.elizabeth.fedihome.ui.view.compose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
	val activeProfile: Profile? = null,
	val isReply: Boolean = false,
	val replyTo: Post? = null,
)

@Suppress("unused")
@HiltViewModel
class ComposeViewModel @Inject constructor(
	private val createPostUseCase: CreatePostUseCase,
	private val authRepository: AuthRepository,
	private val postRepository: PostRepository,
	private val profileRepository: ProfileRepository,
	private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
	private val _uiState = MutableStateFlow(ComposeUiState())
	val uiState = combine(authRepository.activeAccount, _uiState) { activeAccount, uiState ->
		val isReply = savedStateHandle.contains("replyTo")
		uiState.copy(
			activeProfile = profileRepository.getByFullUsername(activeAccount),
			isReply = isReply,
			replyTo = if (isReply) postRepository.getPost(savedStateHandle["replyTo"]!!) else null
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

}
