package sh.elizabeth.wastodon.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import sh.elizabeth.wastodon.util.Dispatcher
import sh.elizabeth.wastodon.util.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {
    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(
        @Dispatcher(Dispatchers.Default) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}
