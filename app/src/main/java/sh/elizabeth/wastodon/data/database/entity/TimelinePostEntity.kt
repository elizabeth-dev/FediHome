package sh.elizabeth.wastodon.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	indices = [Index(
		value = ["profileIdentifier", "timelinePostId"],
		unique = true
	), Index(value = ["timelinePostId"])],
	foreignKeys = [ForeignKey(
		entity = PostEntity::class,
		parentColumns = ["postId"],
		childColumns = ["timelinePostId"]
	)]
)
data class TimelinePostCrossRefEntity(
	@PrimaryKey(autoGenerate = true) var timelinePostRow: Long = 0,
	val profileIdentifier: String,
	val timelinePostId: String,
)
