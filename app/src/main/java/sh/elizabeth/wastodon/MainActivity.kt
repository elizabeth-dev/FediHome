package sh.elizabeth.wastodon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.HttpClient
import sh.elizabeth.wastodon.data.datasource.InternalDataLocalDataSource
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	@Inject
	lateinit var settingsLocalDataSource: InternalDataLocalDataSource

	@Inject
	lateinit var httpClient: HttpClient

	@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)

		setContent {
			WastodonApp(windowSizeClass = calculateWindowSizeClass(this))
		}
	}
}
