plugins {
	kotlin("kapt")
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("com.google.dagger.hilt.android")
	id("com.google.protobuf")
	kotlin("plugin.serialization")
	id("com.google.devtools.ksp")
}

android {
	namespace = "sh.elizabeth.wastodon"
	compileSdk = 33

	defaultConfig {
		applicationId = "sh.elizabeth.wastodon"
		minSdk = 24
		targetSdk = 33
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		isCoreLibraryDesugaringEnabled = true

		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.1"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

androidComponents {
	beforeVariants {

		android.sourceSets.getByName(it.name) {
			java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
			kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
		}
	}
}

afterEvaluate {
	tasks.named("kspDebugKotlin").configure { dependsOn("generateDebugProto") }
	tasks.named("kspReleaseKotlin").configure { dependsOn("generateReleaseProto") }
}

kapt {
	correctErrorTypes = true
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.23.4"
	}
	generateProtoTasks {
		all().forEach { task ->
			task.builtins {
				register("java") {
					option("lite")
				}
				register("kotlin") {
					option("lite")
				}
			}
		}
	}
}

dependencies {
	implementation("androidx.core:core-ktx:1.9.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
	implementation("androidx.activity:activity-compose:1.7.2")
	implementation(platform("androidx.compose:compose-bom:2023.03.00"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")

	implementation("androidx.navigation:navigation-compose:2.6.0")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0-beta01")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
	implementation("androidx.compose.material3:material3-window-size-class")

	implementation("com.google.dagger:hilt-android:2.47")
	kapt("com.google.dagger:hilt-android-compiler:2.47")
	implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

	implementation("androidx.datastore:datastore:1.0.0")
	implementation("com.google.protobuf:protobuf-kotlin-lite:3.23.4")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")

	implementation("androidx.browser:browser:1.5.0")

	// Needed for:
	// java.time when targeting API Level <26
	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

	val ktorVersion = "2.3.3"
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

	val roomVersion = "2.5.2"
	implementation("androidx.room:room-runtime:$roomVersion")
	ksp("androidx.room:room-compiler:$roomVersion")
	implementation("androidx.room:room-ktx:$roomVersion")
	//implementation("androidx.room:room-paging:$room_version")
}
