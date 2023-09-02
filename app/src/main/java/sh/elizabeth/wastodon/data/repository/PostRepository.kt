package sh.elizabeth.wastodon.data.repository

import sh.elizabeth.wastodon.data.datasource.PostLocalDataSource
import sh.elizabeth.wastodon.model.Post
import javax.inject.Inject

class PostRepository @Inject constructor(private val postLocalDataSource: PostLocalDataSource) {
	suspend fun insertOrReplace(vararg posts: Post) {
		postLocalDataSource.insertOrReplace(*posts)
	}
}
