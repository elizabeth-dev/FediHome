package sh.elizabeth.fedihome.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sh.elizabeth.fedihome.PostEmojiCrossRef
import sh.elizabeth.fedihome.PostEntity
import sh.elizabeth.fedihome.data.database.AppDatabase
import sh.elizabeth.fedihome.data.database.entity.PollChoiceEntity
import sh.elizabeth.fedihome.data.database.entity.PollEntity
import sh.elizabeth.fedihome.data.database.entity.toPostDomain
import sh.elizabeth.fedihome.model.Post
import javax.inject.Inject

class PostLocalDataSource @Inject constructor(private val appDatabase: AppDatabase) {
	fun insertOrReplace(vararg posts: Post) {
		appDatabase.postQueries.transaction {
			posts.forEach { post ->
				appDatabase.postQueries.insertOrReplace(post.toEntity())
			}
		}
	}

	fun insertOrReplaceEmojiCrossRef(vararg refs: PostEmojiCrossRef) =
		appDatabase.postQueries.transaction {
			refs.forEach { ref ->
				appDatabase.postQueries.insertOrReplacePostEmojiCrossRef(
					ref
				)
			}
		}

	fun getPost(postId: String): Flow<Post?> =
		appDatabase.postQueries.getPostById(postId)
			.asFlow()
			.mapToOneOrNull(Dispatchers.IO)
			.map { it?.toPostDomain() }

	fun getPostSingle(postId: String): Post? =
		appDatabase.postQueries.getPostById(postId)
			.executeAsOneOrNull()
			?.toPostDomain()

	fun getPostsByProfile(profileId: String) =
		appDatabase.postQueries.getPostByAuthor(profileId)
			.asFlow()
			.mapToList(Dispatchers.IO)
			.map { posts -> posts.map { it.toPostDomain() } }
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
