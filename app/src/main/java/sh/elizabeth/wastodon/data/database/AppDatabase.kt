package sh.elizabeth.wastodon.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import sh.elizabeth.wastodon.data.database.dao.PostDao
import sh.elizabeth.wastodon.data.database.dao.ProfileDao
import sh.elizabeth.wastodon.data.database.dao.TimelineDao
import sh.elizabeth.wastodon.data.database.entity.PostEntity
import sh.elizabeth.wastodon.data.database.entity.ProfileEntity
import sh.elizabeth.wastodon.data.database.entity.TimelinePostCrossRefEntity
import java.time.Instant

@Database(entities = [ProfileEntity::class, PostEntity::class, TimelinePostCrossRefEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun profileDao(): ProfileDao
	abstract fun postDao(): PostDao
	abstract fun timelineDao(): TimelineDao
}

class Converters {
	@TypeConverter
	fun fromTimestamp(value: Long?): Instant? {
		return value?.let { Instant.ofEpochMilli(it) }
	}

	@TypeConverter
	fun instantToTimestamp(instant: Instant?): Long? {
		return instant?.toEpochMilli()
	}
}
