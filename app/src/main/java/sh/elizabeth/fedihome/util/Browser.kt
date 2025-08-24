package sh.elizabeth.fedihome.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent

fun openLinkInCustomTab(uri: Uri, context: Context) {
	val customTabsIntent = CustomTabsIntent.Builder()
		.setShowTitle(true)
		.build()

	try {
		customTabsIntent.launchUrl(context, uri)
	} catch (e: ActivityNotFoundException) {
		Log.w("openLinkinCustomTab", "Activity was not found for intent $customTabsIntent")
		openLinkInBrowser(uri, context)
	}
}

fun openLinkInBrowser(uri: Uri?, context: Context) {
	val intent = Intent(Intent.ACTION_VIEW, uri)
	intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
	try {
		context.startActivity(intent)
	} catch (e: ActivityNotFoundException) {
		Log.w("openLinkInBrowser", "Activity was not found for intent, $intent")
	}
}
