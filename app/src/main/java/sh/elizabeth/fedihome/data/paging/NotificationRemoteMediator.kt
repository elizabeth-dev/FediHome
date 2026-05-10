package sh.elizabeth.fedihome.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import sh.elizabeth.fedihome.GetNotificationsByAccount
import sh.elizabeth.fedihome.data.repository.NotificationRepository

@OptIn(ExperimentalPagingApi::class)
class NotificationRemoteMediator(
	private val activeAccount: String,
	private val notificationRepository: NotificationRepository,
) : RemoteMediator<String, GetNotificationsByAccount>() {
	override suspend fun load(
		loadType: LoadType,
		state: PagingState<String, GetNotificationsByAccount>,
	): MediatorResult {
		val loadKey = when (loadType) {
			LoadType.REFRESH -> null
			LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
			LoadType.APPEND -> {
				val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(
					endOfPaginationReached = true
				)

				lastItem.notificationId
			}
		}

		val newIds =
			notificationRepository.fetchNotifications(activeAccount, loadKey, state.config.pageSize)

		return MediatorResult.Success(
			endOfPaginationReached = newIds.size < state.config.pageSize
		)
	}
}