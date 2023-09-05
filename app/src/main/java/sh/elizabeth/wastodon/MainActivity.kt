package sh.elizabeth.wastodon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import sh.elizabeth.wastodon.data.datasource.SettingsLocalDataSource
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	@Inject
	lateinit var settingsLocalDataSource: SettingsLocalDataSource

	@Inject
	lateinit var httpClient: HttpClient

	@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)

		// Not sure if having this here is the cleanest thing, but it works for now
		lifecycleScope.launch {
			settingsLocalDataSource.settingsData.map { it.accessTokens[it.activeAccount] }
				.distinctUntilChanged()
				.collect {
					httpClient.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
						.first()
						.clearToken()
				}
		}

		setContent {
			WastodonApp(windowSizeClass = calculateWindowSizeClass(this))
		}
	}
}
