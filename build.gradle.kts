// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	id("com.android.application") version "9.2.0" apply false
	id("com.google.dagger.hilt.android") version "2.59.2" apply false
	kotlin("plugin.serialization") version "2.3.21" apply false
	id("com.google.protobuf") version "0.10.0" apply false
	id("com.google.devtools.ksp") version "2.3.7" apply false
	id("app.cash.sqldelight") version "2.3.2" apply false
	id("com.google.gms.google-services") version "4.4.4" apply false
	id("org.jetbrains.kotlin.plugin.compose") version "2.3.21" apply false
}
val ktorClientAuthVersion by extra("2.3.6")
