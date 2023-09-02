package sh.elizabeth.wastodon.data.datasource

import sh.elizabeth.wastodon.data.database.dao.PostDao
import sh.elizabeth.wastodon.data.database.entity.PostEntity
import sh.elizabeth.wastodon.data.database.entity.toPostDomain
import sh.elizabeth.wastodon.model.Post
import javax.inject.Inject

class PostLocalDataSource @Inject constructor(private val postDao: PostDao) {
	suspend fun insertOrReplace(vararg posts: Post): List<Long> =
		postDao.insertOrReplace(*posts.map(Post::toEntity).toTypedArray())

	suspend fun getPost(postId: String): Post? =
		postDao.getPost(postId)?.toPostDomain()
}

fun Post.toEntity() = PostEntity(
	postId = id,
	createdAt = createdAt,
	updatedAt = updatedAt,
	cw = cw,
	text = text,
	authorId = author.id,
)
