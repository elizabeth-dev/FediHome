package sh.elizabeth.wastodon.data.datasource

import kotlinx.coroutines.flow.map
import sh.elizabeth.wastodon.data.database.dao.PostDao
import sh.elizabeth.wastodon.data.database.entity.EnrichedPost
import sh.elizabeth.wastodon.data.database.entity.PollChoiceEntity
import sh.elizabeth.wastodon.data.database.entity.PollEntity
import sh.elizabeth.wastodon.data.database.entity.PostEmojiCrossRef
import sh.elizabeth.wastodon.data.database.entity.PostEntity
import sh.elizabeth.wastodon.data.database.entity.toPostDomain
import sh.elizabeth.wastodon.model.Post
import javax.inject.Inject

class PostLocalDataSource @Inject constructor(private val postDao: PostDao) {
	suspend fun insertOrReplace(vararg posts: Post): List<Long> =
		postDao.insertOrReplace(*posts.map(Post::toEntity).toTypedArray())

	suspend fun insertOrReplaceEmojiCrossRef(vararg refs: PostEmojiCrossRef): List<Long> =
		postDao.insertOrReplaceEmojiCrossRef(*refs)

	suspend fun getPost(postId: String): Post? = postDao.getPost(postId)?.toPostDomain()

	fun getPostFlow(postId: String) = postDao.getPostFlow(postId).map(EnrichedPost::toPostDomain)

	fun getPostsByProfileFlow(profileId: String) =
		postDao.getPostsByProfileFlow(profileId).map { it.map(EnrichedPost::toPostDomain) }
}

fun Post.toEntity() = PostEntity(
	postId = id,
	createdAt = createdAt,
	updatedAt = updatedAt,
	cw = cw,
	text = text,
	authorId = author.id,
	quoteId = quote?.id,
	poll = if (poll != null) PollEntity(
		id = poll.id, choices = poll.choices.map {
			PollChoiceEntity(
				text = it.text, votes = it.votes, isVoted = it.isVoted
			)
		}, expiresAt = poll.expiresAt, multiple = poll.multiple
	) else null
)
