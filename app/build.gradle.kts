plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("com.google.dagger.hilt.android")
	id("com.google.protobuf")
	kotlin("plugin.serialization")
	id("com.google.devtools.ksp")
}

android {
	namespace = "sh.elizabeth.fedihome"
	compileSdk = 34

	defaultConfig {
		applicationId = "sh.elizabeth.fedihome"
		minSdk = 24
		targetSdk = 34
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
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
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
		kotlinCompilerExtensionVersion = "1.5.4"
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

ksp {
	arg("room.schemaLocation", "$projectDir/schemas")
	arg("room.incremental", "true")
	arg("room.generateKotlin", "true")
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
	implementation("androidx.core:core-ktx:1.12.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0-rc01")
	implementation("androidx.activity:activity-compose:1.8.1")
	implementation(platform("androidx.compose:compose-bom:2023.10.01"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material:material")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.compose.material:material-icons-extended")
	implementation("androidx.compose.material3:material3-window-size-class")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")

	implementation("androidx.navigation:navigation-compose:2.7.5")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0-rc01")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0-rc01")
	implementation("androidx.compose.material3:material3-window-size-class")

	implementation("com.google.dagger:hilt-android:2.48.1")
	ksp("com.google.dagger:hilt-android-compiler:2.48.1")
	implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

	implementation("androidx.datastore:datastore:1.0.0")
	implementation("com.google.protobuf:protobuf-kotlin-lite:3.25.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

	implementation("androidx.browser:browser:1.7.0")

	// Needed for:
	// java.time when targeting API Level <26
	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

	val ktorVersion = "2.3.6"
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
	implementation("io.ktor:ktor-client-auth:$ktorVersion")

	val roomVersion = "2.6.0"
	implementation("androidx.room:room-runtime:$roomVersion")
	ksp("androidx.room:room-compiler:$roomVersion")
	implementation("androidx.room:room-ktx:$roomVersion")
	//implementation("androidx.room:room-paging:$room_version")
	implementation("com.google.code.gson:gson:2.10.1")

	implementation("com.github.bumptech.glide:glide:4.16.0")
	implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
	implementation("com.github.bumptech.glide:okhttp3-integration:4.16.0")
	ksp("com.github.bumptech.glide:ksp:4.16.0")
	implementation("com.vanniktech:blurhash:0.1.0")
}
