// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	id("com.android.application") version "8.3.1" apply false
	id("org.jetbrains.kotlin.android") version "1.9.20" apply false
	id("com.google.dagger.hilt.android") version "2.48.1" apply false
	kotlin("plugin.serialization") version "1.9.20" apply false
	id("com.google.protobuf") version "0.9.4" apply false
	id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
	id("app.cash.sqldelight") version "2.0.1" apply false
}
val ktorClientAuthVersion by extra("2.3.6")
