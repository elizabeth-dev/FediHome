package sh.elizabeth.fedihome.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import sh.elizabeth.fedihome.GetTimelinePosts
import sh.elizabeth.fedihome.data.repository.TimelineRepository

@OptIn(ExperimentalPagingApi::class)
class TimelineRemoteMediator(
	private val activeAccount: String,
	private val timelineRepository: TimelineRepository,
) : RemoteMediator<String, GetTimelinePosts>() {
	override suspend fun load(
		loadType: LoadType,
		state: PagingState<String, GetTimelinePosts>,
	): MediatorResult {
		val loadKey = when (loadType) {
			LoadType.REFRESH -> null
			LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
			LoadType.APPEND -> {
				val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(
					endOfPaginationReached = true
				)

				lastItem.postId
			}
		}

		val newIds = timelineRepository.fetchTimeline(activeAccount, loadKey, state.config.pageSize)

		return MediatorResult.Success(
			endOfPaginationReached = newIds.size < state.config.pageSize
		)
	}
}