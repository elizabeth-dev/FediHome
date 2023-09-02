package sh.elizabeth.wastodon.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import sh.elizabeth.wastodon.Settings
import sh.elizabeth.wastodon.data.datasource.SettingsSerializer
import sh.elizabeth.wastodon.util.Dispatcher
import sh.elizabeth.wastodon.util.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

    @Singleton
    @Provides
    fun provideSettingsDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(Dispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        settingsSerializer: SettingsSerializer,
    ): DataStore<Settings> =
        DataStoreFactory.create(
            serializer = settingsSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
        ) {
            context.dataStoreFile("settings.pb")
        }
}
