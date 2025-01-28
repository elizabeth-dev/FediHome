import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("com.google.dagger.hilt.android")
	id("com.google.protobuf")
	kotlin("plugin.serialization")
	id("com.google.devtools.ksp")
	id("app.cash.sqldelight")
	id("com.google.gms.google-services")
	id("org.jetbrains.kotlin.plugin.compose")
}

android {
	namespace = "sh.elizabeth.fedihome"
	compileSdk = 35
	buildToolsVersion = "35.0.0"
	ndkVersion = "26.1.10909125"

	defaultConfig {
		applicationId = "sh.elizabeth.fedihome"
		minSdk = 24
		targetSdk = 35
		versionCode = 1
		versionName = "0.0.1"

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

		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
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
			java.srcDir(layout.buildDirectory.dir("generated/source/proto/${it.name}/java"))
			kotlin.srcDir(layout.buildDirectory.dir("generated/source/proto/${it.name}/kotlin"))
		}
	}
	onVariants(selector().all()) { variant ->
		afterEvaluate {
			val capName = variant.name.replaceFirstChar { it.uppercase() }
			tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
				setSource(
					tasks.getByName("generate${capName}AppDatabaseInterface").outputs
				)
			}
		}
	}
}

afterEvaluate {
	tasks.named("kspDebugKotlin").configure {
		dependsOn(
			"generateDebugProto"
		)
	}
	tasks.named("kspReleaseKotlin").configure {
		dependsOn("generateReleaseProto")
	}
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

sqldelight {
	databases {
		create("AppDatabase") {
			packageName.set("sh.elizabeth.fedihome.data.database")
		}
	}
}

dependencies {
	implementation("androidx.core:core-ktx:1.15.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
	implementation("androidx.activity:activity-compose:1.10.0")
	implementation(platform("androidx.compose:compose-bom:2025.01.00"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material:material")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.compose.material:material-icons-extended")
	implementation("androidx.compose.material3:material3-window-size-class")
	testImplementation("junit:junit:4.13.2")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.2.1")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
	androidTestImplementation(platform("androidx.compose:compose-bom:2025.01.00"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")

	implementation("androidx.navigation:navigation-compose:2.8.5")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
	implementation("androidx.compose.material3:material3-window-size-class")

	implementation("com.google.dagger:hilt-android:2.52")
	ksp("com.google.dagger:hilt-android-compiler:2.52")
	ksp("androidx.hilt:hilt-compiler:1.2.0")
	implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

	implementation("androidx.datastore:datastore:1.1.2")
	implementation("com.google.protobuf:protobuf-kotlin-lite:4.28.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

	implementation("androidx.browser:browser:1.8.0")

	// Needed for:
	// java.time when targeting API Level <26
	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")

	val ktorVersion = "2.3.12"
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
	implementation("io.ktor:ktor-client-auth:$ktorVersion")

	// Coil + BlurHash
	implementation(platform("io.coil-kt:coil-bom:2.7.0"))
	implementation("io.coil-kt:coil")
	implementation("io.coil-kt:coil-compose")
	implementation("io.coil-kt:coil-gif")
	implementation("io.coil-kt:coil-svg")
	testImplementation("io.coil-kt:coil-test")
	androidTestImplementation("io.coil-kt:coil-test:2.7.0")
	implementation("com.vanniktech:blurhash:0.3.0")

	// SQLDelight
	implementation("app.cash.sqldelight:android-driver:2.0.1")
	implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")
	implementation("com.google.code.gson:gson:2.11.0")

	// Firebase
	implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
	implementation("com.google.firebase:firebase-analytics")
	implementation("com.google.firebase:firebase-messaging")

	// Work Manager (used for firebase messaging)
	val work_version = "2.10.0"
	implementation("androidx.work:work-runtime-ktx:$work_version")
	implementation("androidx.hilt:hilt-work:1.2.0")

}
