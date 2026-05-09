package sh.elizabeth.fedihome.ui.routes.dashboard.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.data.repository.NotificationRepository
import sh.elizabeth.fedihome.data.repository.PostRepository
import sh.elizabeth.fedihome.domain.VotePollUseCase
import sh.elizabeth.fedihome.model.Notification
import sh.elizabeth.fedihome.util.viewmodel.PostHandlingViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
	authRepository: AuthRepository,
	private val notificationRepository: NotificationRepository,
	override val votePollUseCase: VotePollUseCase,
	override val postRepository: PostRepository,
) : PostHandlingViewModel, ViewModel() {
	override val coroutineHandlingScope: CoroutineScope
		get() = viewModelScope

	val activeAccount: StateFlow<String> = authRepository.activeAccount.stateIn(
		viewModelScope, SharingStarted.Eagerly, ""
	)

	@OptIn(ExperimentalCoroutinesApi::class)
	val pagingFlow: Flow<PagingData<Notification>> = activeAccount
		.flatMapLatest { account ->
			if (account.isBlank()) {
				flowOf(PagingData.empty())
			}
			else {
				notificationRepository.getPagedNotifications(
					activeAccount = account
				)
			}
		}
		.cachedIn(viewModelScope)
}
