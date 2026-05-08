package sh.elizabeth.fedihome.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.database.entity.toPostDomain
import sh.elizabeth.fedihome.data.repository.TimelineRepository
import sh.elizabeth.fedihome.model.Post

class TimelinePagingSource(
	private val activeAccount: String,
	private val profileIdentifier: String,
	private val timelineRepository: TimelineRepository,
	private val appDatabase: AppDatabase,
) : PagingSource<Int, Post>() {

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
		return try {
			val offset = params.key ?: 0
			val limit = params.loadSize.toLong()

			// On first load (offset 0), always fetch latest from network
			if (offset == 0) {
				try {
					val fetchedIds = timelineRepository.fetchTimeline(
						activeAccount = activeAccount,
						profileIdentifier = profileIdentifier,
						untilId = null,
						limit = params.loadSize
					)

					// Store the cursor for where to fetch next from the API
					if (fetchedIds.isNotEmpty()) {
						appDatabase.timelineRemoteKeyQueries.insertOrReplace(
							profileId = profileIdentifier,
							nextKey = fetchedIds.last(),
						)
					}
				} catch (_: Exception) {
					// Network failed — fall through to cached data
				}
			}

			// Load from local DB
			val dbResults = appDatabase.timelinePostQueries
				.getTimelinePosts(profileIdentifier, limit, offset.toLong())
				.executeAsList()
				.map { it.toPostDomain() }

			// Check if we need to fetch more from the API.
			// We need to fetch when the DB page is not full OR when
			// the last item in this page is at or past the remote key
			// (meaning we've reached the edge of what was fetched from the API).
			if (dbResults.size < params.loadSize) {
				// DB doesn't have enough posts — fetch from network
				val remoteKey = appDatabase.timelineRemoteKeyQueries
					.getByProfileId(profileIdentifier)
					.executeAsOneOrNull()

				val nextKey = remoteKey?.nextKey
				if (nextKey != null) {
					try {
						val fetchedIds = timelineRepository.fetchTimeline(
							activeAccount = activeAccount,
							profileIdentifier = profileIdentifier,
							untilId = nextKey,
							limit = params.loadSize
						)

						if (fetchedIds.isNotEmpty()) {
							appDatabase.timelineRemoteKeyQueries.insertOrReplace(
								profileId = profileIdentifier,
								nextKey = fetchedIds.last(),
							)

							// Re-query to include newly fetched posts
							val updatedResults = appDatabase.timelinePostQueries
								.getTimelinePosts(profileIdentifier, limit, offset.toLong())
								.executeAsList()
								.map { it.toPostDomain() }

							return LoadResult.Page(
								data = updatedResults,
								prevKey = if (offset == 0) null else offset - params.loadSize,
								nextKey = if (updatedResults.isEmpty()) null
								else offset + updatedResults.size
							)
						}
					} catch (_: Exception) {
						// Network failed — return what we have from DB
					}
				}
			}
			else {
				// DB page is full, but check if the last post in this page
				// matches or passes the remote key boundary
				val remoteKey = appDatabase.timelineRemoteKeyQueries
					.getByProfileId(profileIdentifier)
					.executeAsOneOrNull()

				val nextKey = remoteKey?.nextKey
				if (nextKey != null) {
					val lastPost = dbResults.last()
					// If the last post in this page is the remote key boundary,
					// proactively fetch the next batch from the API
					if (lastPost.id == nextKey) {
						try {
							val fetchedIds = timelineRepository.fetchTimeline(
								activeAccount = activeAccount,
								profileIdentifier = profileIdentifier,
								untilId = nextKey,
								limit = params.loadSize
							)

							if (fetchedIds.isNotEmpty()) {
								appDatabase.timelineRemoteKeyQueries.insertOrReplace(
									profileId = profileIdentifier,
									nextKey = fetchedIds.last(),
								)
							}
						} catch (_: Exception) {
							// Network failed — next page will try again
						}
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

	override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
		return state.anchorPosition?.let { anchor ->
			state.closestPageToPosition(anchor)?.prevKey?.plus(state.config.pageSize)
				?: state.closestPageToPosition(anchor)?.nextKey?.minus(state.config.pageSize)
		}
	}
}
