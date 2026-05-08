package sh.elizabeth.fedihome.ui.routes.dashboard.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.data.repository.PostRepository
import sh.elizabeth.fedihome.data.repository.TimelineRepository
import sh.elizabeth.fedihome.domain.RefreshTimelineUseCase
import sh.elizabeth.fedihome.domain.VotePollUseCase
import sh.elizabeth.fedihome.model.Post
import sh.elizabeth.fedihome.util.viewmodel.PostHandlingViewModel
import javax.inject.Inject

sealed interface HomeUiState {
	val isLoading: Boolean
	val activeAccount: String

	data class NoPosts(
		override val isLoading: Boolean,
		override val activeAccount: String,
	) : HomeUiState

	data class HasPosts(
		val posts: List<Post>,
		override val isLoading: Boolean,
		override val activeAccount: String,
	) : HomeUiState
}

private data class HomeViewModelState(
	val isLoading: Boolean = false,
	val pageSize: Long = 20,
	val loadedPages: Int = 1,
	val canLoadMore: Boolean = true,
	val isLoadingMore: Boolean = false,
) {
	val limit: Long get() = pageSize * loadedPages

	fun toUiState(posts: List<Post>?, activeAccount: String = ""): HomeUiState =
		if (posts.isNullOrEmpty()) {
			HomeUiState.NoPosts(
				isLoading = isLoading, activeAccount = activeAccount
			)
		} else {
			HomeUiState.HasPosts(
				posts = posts, isLoading = isLoading, activeAccount = activeAccount
			)
		}

}

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val timelineRepository: TimelineRepository,
	authRepository: AuthRepository,
	private val refreshTimelineUseCase: RefreshTimelineUseCase,
	override val votePollUseCase: VotePollUseCase,
	override val postRepository: PostRepository,
) : PostHandlingViewModel, ViewModel() {
	override val coroutineHandlingScope: CoroutineScope
		get() = viewModelScope
	private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

	@OptIn(ExperimentalCoroutinesApi::class)
	private val timeline = combine(
		authRepository.activeAccount,
		viewModelState
	) { account, state ->
		Pair(account, state.limit)
	}.distinctUntilChanged().flatMapLatest { (account, limit) ->
		timelineRepository.getTimeline(account, limit = limit, offset = 0)
	}.distinctUntilChanged()

	val uiState = combine(
		viewModelState, authRepository.activeAccount, timeline
	) { state, activeAccount, posts ->
		state.toUiState(posts, activeAccount)
	}.stateIn(
		viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState(
			posts = null,
		)
	)

	fun refreshTimeline(profileIdentifier: String) {
		viewModelState.update { it.copy(isLoading = true, loadedPages = 1) }
		viewModelScope.launch {
			refreshTimelineUseCase(profileIdentifier)
			viewModelState.update { it.copy(isLoading = false) }
		}
	}

	fun loadMore() {
		if (viewModelState.value.isLoadingMore || !viewModelState.value.canLoadMore) return
		viewModelState.update { it.copy(isLoadingMore = true, loadedPages = it.loadedPages + 1) }

		// Also fetch more from remote API using the oldest post as cursor
		val currentState = uiState.value
		if (currentState is HomeUiState.HasPosts && currentState.posts.isNotEmpty()) {
			val oldestPostId = currentState.posts.last().id
			viewModelScope.launch {
				try {
					timelineRepository.fetchTimeline(
						activeAccount = currentState.activeAccount,
						profileIdentifier = currentState.activeAccount,
						untilId = oldestPostId
					)
				} finally {
					viewModelState.update { it.copy(isLoadingMore = false) }
				}
			}
		}
		else {
			viewModelState.update { it.copy(isLoadingMore = false) }
		}
	}
}
