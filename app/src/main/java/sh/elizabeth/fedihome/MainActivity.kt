package sh.elizabeth.fedihome

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.HttpClient
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
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
			FediHomeApp(windowSizeClass = calculateWindowSizeClass(this))
		}

		// FIXME: move this to the login flow, also check for Play Services availability
		askNotificationPermission()
	}

	private val requestPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission(),
	) { isGranted: Boolean ->
		if (isGranted) {
			// TODO: FCM SDK (and your app) can post notifications.
		} else {
			// TODO: Inform user that that your app will not show notifications.
		}
	}

	private fun askNotificationPermission() {
		// This is only necessary for API level >= 33 (TIRAMISU)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (ContextCompat.checkSelfPermission(
					this,
					Manifest.permission.POST_NOTIFICATIONS
				) == PackageManager.PERMISSION_GRANTED
			) {
				// TODO: FCM SDK (and your app) can post notifications.
			} else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
				// TODO: display an educational UI explaining to the user the features that will be enabled
				//       by them granting the POST_NOTIFICATION permission. This UI should provide the user
				//       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
				//       If the user selects "No thanks," allow the user to continue without notifications.
				requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

			} else {
				// Directly ask for the permission
				requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
			}
		}
	}
}
