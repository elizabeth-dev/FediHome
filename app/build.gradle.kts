import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("com.google.dagger.hilt.android")
	id("com.google.protobuf")
	kotlin("plugin.serialization")
	id("com.google.devtools.ksp")
	id("app.cash.sqldelight")
}

android {
	namespace = "sh.elizabeth.fedihome"
	compileSdk = 34
	buildToolsVersion = "34.0.0"
	ndkVersion = "26.1.10909125"

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
		kotlinCompilerExtensionVersion = "1.5.7"
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
	implementation("androidx.core:core-ktx:1.12.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
	implementation("androidx.activity:activity-compose:1.8.2")
	implementation(platform("androidx.compose:compose-bom:2024.03.00"))
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
	androidTestImplementation(platform("androidx.compose:compose-bom:2024.03.00"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")

	implementation("androidx.navigation:navigation-compose:2.7.7")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
	implementation("androidx.compose.material3:material3-window-size-class")

	implementation("com.google.dagger:hilt-android:2.51")
	ksp("com.google.dagger:hilt-android-compiler:2.48.1")
	implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

	implementation("androidx.datastore:datastore:1.0.0")
	implementation("com.google.protobuf:protobuf-kotlin-lite:3.25.2")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

	implementation("androidx.browser:browser:1.8.0")

	// Needed for:
	// java.time when targeting API Level <26
	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

	val ktorVersion = "2.3.6"
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
	implementation("io.ktor:ktor-client-auth:$ktorVersion")

	implementation("com.github.bumptech.glide:glide:4.16.0")
	implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
	implementation("com.github.bumptech.glide:okhttp3-integration:4.16.0")
	ksp("com.github.bumptech.glide:ksp:4.16.0")
	implementation("com.vanniktech:blurhash:0.1.0")

	implementation("app.cash.sqldelight:android-driver:2.0.1")
	implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")
	implementation("com.google.code.gson:gson:2.10.1")

}
