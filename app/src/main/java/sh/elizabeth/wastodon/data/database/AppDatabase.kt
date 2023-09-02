package sh.elizabeth.wastodon.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import sh.elizabeth.wastodon.data.database.dao.ProfileDao
import sh.elizabeth.wastodon.data.database.entity.ProfileEntity

@Database(entities = [ProfileEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
	abstract fun profileDao(): ProfileDao
}
