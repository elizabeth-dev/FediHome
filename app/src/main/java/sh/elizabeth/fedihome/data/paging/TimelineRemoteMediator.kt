package sh.elizabeth.fedihome.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import sh.elizabeth.fedihome.GetTimelinePosts
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.datasource.TimelineLocalDataSource
import sh.elizabeth.fedihome.data.repository.TimelineRepository

@OptIn(ExperimentalPagingApi::class)
class TimelineRemoteMediator(
	private val activeAccount: String,
	private val profileIdentifier: String,
	private val timelineRepository: TimelineRepository,
	private val timelineLocalDataSource: TimelineLocalDataSource,
	private val appDatabase: AppDatabase,
) : RemoteMediator<Int, GetTimelinePosts>() {

	private var hasRefreshed = false

	override suspend fun initialize(): InitializeAction {
		return InitializeAction.LAUNCH_INITIAL_REFRESH
	}

	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, GetTimelinePosts>,
	): MediatorResult {
		return try {
			when (loadType) {
				LoadType.REFRESH -> {
					if (hasRefreshed) {
						// After the first refresh, skip subsequent automatic refreshes
						// triggered by PagingSource invalidation. The user can still
						// pull-to-refresh which creates a new Pager.
						return MediatorResult.Success(endOfPaginationReached = false)
					}

					val fetchedIds = timelineRepository.fetchTimeline(
						activeAccount = activeAccount,
						profileIdentifier = profileIdentifier,
						untilId = null,
						limit = state.config.pageSize
					)

					appDatabase.timelineRemoteKeyQueries.insertOrReplace(
						profileId = profileIdentifier,
						nextKey = fetchedIds.lastOrNull(),
					)

					hasRefreshed = true
					MediatorResult.Success(endOfPaginationReached = fetchedIds.isEmpty())
				}

				LoadType.PREPEND -> {
					MediatorResult.Success(endOfPaginationReached = true)
				}

				LoadType.APPEND -> {
					val remoteKey = appDatabase.timelineRemoteKeyQueries
						.getByProfileId(profileIdentifier)
						.executeAsOneOrNull()
						?: return MediatorResult.Success(endOfPaginationReached = true)

					val nextKey = remoteKey.nextKey
						?: return MediatorResult.Success(endOfPaginationReached = true)

					val fetchedIds = timelineRepository.fetchTimeline(
						activeAccount = activeAccount,
						profileIdentifier = profileIdentifier,
						untilId = nextKey,
						limit = state.config.pageSize
					)

					val endReached = fetchedIds.isEmpty()

					appDatabase.timelineRemoteKeyQueries.insertOrReplace(
						profileId = profileIdentifier,
						nextKey = if (endReached) null else fetchedIds.lastOrNull(),
					)

					MediatorResult.Success(endOfPaginationReached = endReached)
				}
			}
		} catch (e: Exception) {
			MediatorResult.Error(e)
		}
	}
}
