package sh.elizabeth.fedihome.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
	primaryKeys = ["profileId", "fullEmojiId"], foreignKeys = [ForeignKey(
		entity = ProfileEntity::class,
		parentColumns = ["profileId"],
		childColumns = ["profileId"],
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = EmojiEntity::class,
		parentColumns = ["fullEmojiId"],
		childColumns = ["fullEmojiId"],
		onDelete = ForeignKey.CASCADE
	)], indices = [Index(
		value = ["profileId"]
	), Index(
		value = ["fullEmojiId"]
	)]
)
data class ProfileEmojiCrossRef(
	val profileId: String,
	val fullEmojiId: String,
)
