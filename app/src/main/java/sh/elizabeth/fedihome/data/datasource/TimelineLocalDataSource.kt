package sh.elizabeth.fedihome.data.datasource

import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.model.TimelinePostItemType
import javax.inject.Inject

class TimelineLocalDataSource @Inject constructor(private val appDatabase: AppDatabase) {
	fun insert(profileIdentifier: String, vararg posts: String) {
		posts.forEach { post ->
			appDatabase.timelinePostQueries.insert(
				timelineProfileId = profileIdentifier,
				postId = post,
				type = TimelinePostItemType.POST
			)
		}
	}
}
