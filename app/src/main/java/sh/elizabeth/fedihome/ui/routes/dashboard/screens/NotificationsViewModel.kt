package sh.elizabeth.fedihome.ui.routes.dashboard.screens


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.database.entity.toNotificationPagingItemDomain
import sh.elizabeth.fedihome.data.paging.NotificationPagingSource
import sh.elizabeth.fedihome.data.paging.NotificationRemoteMediator
import sh.elizabeth.fedihome.data.repository.AuthRepository
import sh.elizabeth.fedihome.data.repository.NotificationRepository
import sh.elizabeth.fedihome.data.repository.PostRepository
import sh.elizabeth.fedihome.domain.VotePollUseCase
import sh.elizabeth.fedihome.model.NotificationPagingItem
import sh.elizabeth.fedihome.util.viewmodel.PostHandlingViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
	authRepository: AuthRepository,
	private val notificationRepository: NotificationRepository,
	override val votePollUseCase: VotePollUseCase,
	override val postRepository: PostRepository,
	val appDatabase: AppDatabase,
) : PostHandlingViewModel, ViewModel() {
	override val coroutineHandlingScope: CoroutineScope
		get() = viewModelScope

	val activeAccount: StateFlow<String> = authRepository.activeAccount.stateIn(
		viewModelScope, SharingStarted.Eagerly, ""
	)

	@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
	val pagingFlow: Flow<PagingData<NotificationPagingItem>> =
		activeAccount.flatMapLatest { account ->
			if (account.isBlank()) {
				flowOf(PagingData.empty())
			}
			else {
				var currentPagingSource: NotificationPagingSource? = null
				Pager(
					config = PagingConfig(
						pageSize = 20,
						enablePlaceholders = false,
						initialLoadSize = 20,
						prefetchDistance = 20
					), pagingSourceFactory = {
						NotificationPagingSource(appDatabase, account).also {
							currentPagingSource = it
						}
					}, remoteMediator = NotificationRemoteMediator(
						account,
						notificationRepository,
						getPagingSource = { currentPagingSource }
					)
				).flow.map { pagingData ->
					pagingData.map {
						it.toNotificationPagingItemDomain()
					}
				}
			}
		}.cachedIn(viewModelScope)
}
