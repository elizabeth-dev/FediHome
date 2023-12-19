package sh.elizabeth.fedihome.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import sh.elizabeth.fedihome.util.Dispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
	@Provides
	@Dispatcher(sh.elizabeth.fedihome.util.Dispatchers.IO)
	fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

	@Provides
	@Dispatcher(sh.elizabeth.fedihome.util.Dispatchers.Default)
	fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
