package sh.elizabeth.fedihome.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.database.entity.toNotificationDomain
import sh.elizabeth.fedihome.data.repository.NotificationRepository
import sh.elizabeth.fedihome.model.Notification

class NotificationPagingSource(
	private val activeAccount: String,
	private val notificationRepository: NotificationRepository,
	private val appDatabase: AppDatabase,
) : PagingSource<Int, Notification>() {

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notification> {
		return try {
			val offset = params.key ?: 0
			val limit = params.loadSize.toLong()

			// On first load, fetch latest from network
			if (offset == 0) {
				try {
					val fetchedIds = notificationRepository.fetchNotifications(
						activeAccount = activeAccount,
						untilId = null,
						limit = params.loadSize
					)

					if (fetchedIds.isNotEmpty()) {
						appDatabase.timelineRemoteKeyQueries.insertOrReplaceNotification(
							accountId = activeAccount,
							nextKey = fetchedIds.last(),
						)
					}
				} catch (_: Exception) {
					// Network failed — fall through to cached data
				}
			}

			// Load from local DB
			val dbResults = appDatabase.notificationQueries
				.getNotificationByAccount(activeAccount, limit, offset.toLong())
				.executeAsList()
				.map { it.toNotificationDomain() }

			if (dbResults.size < params.loadSize) {
				// DB doesn't have enough — fetch from network
				val remoteKey = appDatabase.timelineRemoteKeyQueries
					.getNotificationByAccountId(activeAccount)
					.executeAsOneOrNull()

				val nextKey = remoteKey?.nextKey
				if (nextKey != null) {
					try {
						val fetchedIds = notificationRepository.fetchNotifications(
							activeAccount = activeAccount,
							untilId = nextKey,
							limit = params.loadSize
						)

						if (fetchedIds.isNotEmpty()) {
							appDatabase.timelineRemoteKeyQueries.insertOrReplaceNotification(
								accountId = activeAccount,
								nextKey = fetchedIds.last(),
							)

							val updatedResults = appDatabase.notificationQueries
								.getNotificationByAccount(activeAccount, limit, offset.toLong())
								.executeAsList()
								.map { it.toNotificationDomain() }

							return LoadResult.Page(
								data = updatedResults,
								prevKey = if (offset == 0) null else offset - params.loadSize,
								nextKey = if (updatedResults.isEmpty()) null
								else offset + updatedResults.size
							)
						}
					} catch (_: Exception) {
						// Network failed — return what we have
					}
				}
			}
			else {
				// Check boundary
				val remoteKey = appDatabase.timelineRemoteKeyQueries
					.getNotificationByAccountId(activeAccount)
					.executeAsOneOrNull()

				val nextKey = remoteKey?.nextKey
				if (nextKey != null && dbResults.last().id == nextKey) {
					try {
						val fetchedIds = notificationRepository.fetchNotifications(
							activeAccount = activeAccount,
							untilId = nextKey,
							limit = params.loadSize
						)

						if (fetchedIds.isNotEmpty()) {
							appDatabase.timelineRemoteKeyQueries.insertOrReplaceNotification(
								accountId = activeAccount,
								nextKey = fetchedIds.last(),
							)
						}
					} catch (_: Exception) {
						// Network failed
					}
				}
			}

			LoadResult.Page(
				data = dbResults,
				prevKey = if (offset == 0) null else offset - params.loadSize,
				nextKey = if (dbResults.isEmpty()) null else offset + dbResults.size
			)
		} catch (e: Exception) {
			LoadResult.Error(e)
		}
	}

	override fun getRefreshKey(state: PagingState<Int, Notification>): Int? {
		return state.anchorPosition?.let { anchor ->
			state.closestPageToPosition(anchor)?.prevKey?.plus(state.config.pageSize)
				?: state.closestPageToPosition(anchor)?.nextKey?.minus(state.config.pageSize)
		}
	}
}

