package sh.elizabeth.wastodon.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sh.elizabeth.wastodon.data.database.dao.PostDao
import sh.elizabeth.wastodon.data.database.dao.ProfileDao
import sh.elizabeth.wastodon.data.database.dao.TimelineDao
import sh.elizabeth.wastodon.data.database.entity.PollEntity
import sh.elizabeth.wastodon.data.database.entity.PostEntity
import sh.elizabeth.wastodon.data.database.entity.ProfileEntity
import sh.elizabeth.wastodon.data.database.entity.ProfileExtraEntity
import sh.elizabeth.wastodon.data.database.entity.ProfileFieldEntity
import sh.elizabeth.wastodon.data.database.entity.TimelinePostCrossRefEntity
import java.time.Instant

@Database(
	entities = [ProfileEntity::class, ProfileExtraEntity::class, PostEntity::class, TimelinePostCrossRefEntity::class],
	version = 1
)
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

	@TypeConverter
	fun pollFromJson(value: String?): PollEntity? {
		if (value == null) return null

		val type = object : TypeToken<PollEntity>() {}.type
		return Gson().fromJson(value, type)
	}

	@TypeConverter
	fun pollToJson(poll: PollEntity?): String? {
		if (poll == null) return null

		val type = object : TypeToken<PollEntity>() {}.type
		return Gson().toJson(poll, type)
	}

	@TypeConverter
	fun profileFieldListFromJson(value: String?): List<ProfileFieldEntity> {
		if (value == null) return emptyList()

		val listType = object : TypeToken<ArrayList<ProfileFieldEntity?>?>() {}.type
		return Gson().fromJson(value, listType)
	}

	@TypeConverter
	fun profileFieldListToJson(list: List<ProfileFieldEntity?>?): String = Gson().toJson(list)

}
