package sh.elizabeth.fedihome.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sh.elizabeth.fedihome.GetNotificationsByAccount
import sh.elizabeth.fedihome.data.database.AppDatabase

class NotificationPagingSource(
	private val appDatabase: AppDatabase,
	private val forAccount: String,
) : PagingSource<Int, GetNotificationsByAccount>() {
	override val jumpingSupported: Boolean get() = true

	override fun getRefreshKey(state: PagingState<Int, GetNotificationsByAccount>): Int? {
		return state.anchorPosition?.let { anchor ->
			val closestPage = state.closestPageToPosition(anchor)
			closestPage?.prevKey?.plus(state.config.pageSize)
				?: closestPage?.nextKey?.minus(state.config.pageSize)
		}
	}

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GetNotificationsByAccount> {
		return withContext(Dispatchers.IO) {
			try {
				val offset = when (params) {
					is LoadParams.Refresh -> params.key ?: 0
					is LoadParams.Prepend -> params.key
					is LoadParams.Append -> params.key
				}
				val limit = params.loadSize

				val results =
					appDatabase.notificationQueries.getNotificationsByAccount(
						forAccount = forAccount,
						limit = limit.toLong(),
						offset = offset.toLong()
					).executeAsList()

				val prevKey = if (offset > 0) (offset - limit).coerceAtLeast(0) else null
				val nextKey = if (results.size < limit) null else offset + results.size
				Log.d(
					"NotifPaging",
					"load: type=${params::class.simpleName} key=${params.key} offset=$offset limit=$limit results=${results.size} prevKey=$prevKey nextKey=$nextKey"
				)

				LoadResult.Page(
					data = results,
					prevKey = prevKey,
					nextKey = nextKey,
				)
			} catch (e: Exception) {
				Log.e("NotifPaging", "load error", e)
				LoadResult.Error(e)
			}
		}
	}
}