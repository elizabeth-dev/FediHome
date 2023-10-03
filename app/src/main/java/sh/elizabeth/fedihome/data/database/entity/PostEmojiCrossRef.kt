package sh.elizabeth.fedihome.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
	primaryKeys = ["postId", "fullEmojiId"], foreignKeys = [ForeignKey(
		entity = PostEntity::class,
		parentColumns = ["postId"],
		childColumns = ["postId"],
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = EmojiEntity::class,
		parentColumns = ["fullEmojiId"],
		childColumns = ["fullEmojiId"],
		onDelete = ForeignKey.CASCADE
	)], indices = [Index(
		value = ["fullEmojiId"]
	), Index(value = ["postId"])]
)
data class PostEmojiCrossRef(
	val postId: String,
	val fullEmojiId: String,
)
