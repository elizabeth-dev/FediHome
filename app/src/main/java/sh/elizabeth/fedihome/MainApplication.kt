package sh.elizabeth.fedihome

import android.app.Application
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import dagger.hilt.android.HiltAndroidApp
import sh.elizabeth.fedihome.util.AnimatedPngDecoder
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider, SingletonImageLoader.Factory {

	@Inject
	lateinit var workerFactory: HiltWorkerFactory

	override val workManagerConfiguration: Configuration
		get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

	override fun newImageLoader(context: Context): ImageLoader {
		return ImageLoader.Builder(context).components {
			if (SDK_INT >= P) {
				add(AnimatedImageDecoder.Factory())
			} else {
				add(GifDecoder.Factory())
			}
			add(AnimatedPngDecoder.Factory())
		}.build()
	}
}
