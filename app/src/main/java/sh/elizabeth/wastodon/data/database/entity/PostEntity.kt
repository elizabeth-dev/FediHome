package sh.elizabeth.wastodon.data.database.entity

import androidx.room.*
import sh.elizabeth.wastodon.model.Post
import java.time.Instant

@Entity(
	foreignKeys = [ForeignKey(
		entity = ProfileEntity::class,
		parentColumns = ["profileId"],
		childColumns = ["authorId"]
	)],
	indices = [Index("authorId"), Index(
		value = ["postId"],
		unique = true // FIXME: not actually unique, although calckey api might return a unique id. check this.
	)]
)
data class PostEntity(
	@PrimaryKey(autoGenerate = true) var postRow: Long = 0,
	val postId: String,
	val createdAt: Instant,
	val updatedAt: Instant?,
	val cw: String?,
	val text: String,
	val authorId: String,
)

data class PostWithAuthor(
	@Embedded
	val post: PostEntity,
	@Relation(
		parentColumn = "authorId",
		entityColumn = "profileId"
	)
	val author: ProfileEntity,
)

fun PostWithAuthor.toPostDomain() = Post(
	id = post.postId,
	createdAt = post.createdAt,
	updatedAt = post.updatedAt,
	cw = post.cw,
	text = post.text,
	author = author.toDomain(),
)
