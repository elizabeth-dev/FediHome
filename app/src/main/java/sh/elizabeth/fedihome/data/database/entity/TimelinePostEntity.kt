package sh.elizabeth.fedihome.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	indices = [Index(
		value = ["profileIdentifier", "timelinePostId"],
		unique = true
	), Index(value = ["timelinePostId"]), Index(value = ["repostedBy"])],
	foreignKeys = [ForeignKey(
		entity = PostEntity::class,
		parentColumns = ["postId"],
		childColumns = ["timelinePostId"]
	), ForeignKey(
		entity = ProfileEntity::class,
		parentColumns = ["profileId"],
		childColumns = ["repostedBy"]
	)]
)
data class TimelinePostEntity(
	@PrimaryKey(autoGenerate = true) var timelinePostRow: Long = 0,
	val profileIdentifier: String,
	val timelinePostId: String,
	val repostedBy: String?,
)
