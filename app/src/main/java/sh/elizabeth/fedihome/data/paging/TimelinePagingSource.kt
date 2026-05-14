package sh.elizabeth.fedihome.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.cash.sqldelight.TransactionCallbacks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sh.elizabeth.fedihome.GetTimelinePosts
import sh.elizabeth.fedihome.data.database.AppDatabase

class TimelinePagingSource(
	private val appDatabase: AppDatabase,
	private val forAccount: String,
) : PagingSource<String, GetTimelinePosts>() {
	private var pageBoundaries: List<String>? = null
	override val jumpingSupported: Boolean get() = false

	init {
		appDatabase.timelinePostQueries.count(timelineProfileId = forAccount).addListener {
			invalidate()
		}
	}

	override fun getRefreshKey(state: PagingState<String, GetTimelinePosts>): String? {
		val boundaries = pageBoundaries ?: return null
		val last = state.pages.lastOrNull() ?: return null
		val keyIndexFromNext = last.nextKey?.let { boundaries.indexOf(it) - 1 }
		val keyIndexFromPrev = last.prevKey?.let { boundaries.indexOf(it) + 1 }
		val keyIndex = keyIndexFromNext ?: keyIndexFromPrev ?: return null

		return boundaries.getOrNull(keyIndex)
	}

	override suspend fun load(params: LoadParams<String>): LoadResult<String, GetTimelinePosts> {
		return withContext(Dispatchers.IO) {
			try {
				val getPagingSourceLoadResult: TransactionCallbacks.() -> LoadResult<String, GetTimelinePosts> =
					{
						val boundaries =
							pageBoundaries ?: appDatabase.timelinePostQueries.pageBoundaries(
								limit = params.loadSize.toLong(),
								anchor = params.key,
								timelineProfileId = forAccount
							).executeAsList().also { pageBoundaries = it }

						// FIXME: the key is null when we reach the end of the available items, and it causes the list to "jump"
						val key = params.key ?: boundaries.first()

						require(key in boundaries)

						val keyIndex = boundaries.indexOf(key)
						val previousKey = boundaries.getOrNull(keyIndex - 1)
						val nextKey = boundaries.getOrNull(keyIndex + 1)
						val results = appDatabase.timelinePostQueries.getTimelinePosts(
							timelineProfileId = forAccount,
							beginInclusive = key,
							endExclusive = nextKey
						).executeAsList()

						LoadResult.Page(
							data = results,
							prevKey = previousKey,
							nextKey = nextKey,
						)
					}

				appDatabase.transactionWithResult {
					getPagingSourceLoadResult()
				}
			} catch (e: Exception) {
				if (e is IllegalArgumentException) throw e
				LoadResult.Error(e)
			}
		}
	}
}