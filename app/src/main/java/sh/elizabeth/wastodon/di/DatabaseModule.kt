package sh.elizabeth.wastodon.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sh.elizabeth.wastodon.data.database.AppDatabase
import sh.elizabeth.wastodon.data.database.dao.ProfileDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
	@Provides
	@Singleton
	fun provideNiaDatabase(
		@ApplicationContext context: Context,
	): AppDatabase = Room.databaseBuilder(
		context,
		AppDatabase::class.java,
		"app-database",
	).build()

	@Provides
	fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()
}
