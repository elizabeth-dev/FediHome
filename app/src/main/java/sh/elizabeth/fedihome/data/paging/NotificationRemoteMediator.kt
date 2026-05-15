package sh.elizabeth.fedihome.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import sh.elizabeth.fedihome.GetNotificationsByAccount
import sh.elizabeth.fedihome.data.repository.NotificationRepository

@OptIn(ExperimentalPagingApi::class)
class NotificationRemoteMediator(
	private val activeAccount: String,
	private val notificationRepository: NotificationRepository,
	private val getPagingSource: () -> PagingSource<*, *>?,
) : RemoteMediator<Int, GetNotificationsByAccount>() {
	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, GetNotificationsByAccount>,
	): MediatorResult {
		Log.d(
			"NotifMediator",
			"load called: loadType=$loadType, pages=${state.pages.size}, lastItem=${state.lastItemOrNull()?.notificationId}"
		)
		val loadKey = when (loadType) {
			LoadType.REFRESH -> null
			LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
			LoadType.APPEND -> {
				val lastItem = state.lastItemOrNull()
					?: return MediatorResult.Success(endOfPaginationReached = false)

				lastItem.notificationId
			}
		}

		Log.d("NotifMediator", "fetching with loadKey=$loadKey")
		val newIds =
			notificationRepository.fetchNotifications(activeAccount, loadKey, state.config.pageSize)

		Log.d(
			"NotifMediator",
			"fetched ${newIds.size} items, endReached=${newIds.size < state.config.pageSize}"
		)

		getPagingSource()?.invalidate()

		return MediatorResult.Success(
			endOfPaginationReached = newIds.size < state.config.pageSize
		)
	}
}