plugins {
	id("com.android.application")
	id("com.google.devtools.ksp")
	id("com.google.dagger.hilt.android")
	id("com.google.protobuf")
	kotlin("plugin.serialization")
	id("app.cash.sqldelight")
	id("com.google.gms.google-services")
	id("org.jetbrains.kotlin.plugin.compose")
}

android {
	namespace = "sh.elizabeth.fedihome"
	compileSdk = 37
	buildToolsVersion = "37.0.0"
	ndkVersion = "26.1.10909125"

	defaultConfig {
		applicationId = "sh.elizabeth.fedihome"
		minSdk = 24
		targetSdk = 37
		versionCode = 2
		versionName = "0.0.2"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
			)
		}
	}
	compileOptions {
		isCoreLibraryDesugaringEnabled = true

		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
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

kotlin {
	compilerOptions {
		languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3
		jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
	}
}

androidComponents {
//	beforeVariants {
//		android.sourceSets.getByName(it.name) {
//			java.srcDir(layout.buildDirectory.dir("generated/source/proto/${it.name}/java"))
//			kotlin.srcDir(layout.buildDirectory.dir("generated/source/proto/${it.name}/kotlin"))
//		}
//	}
//	onVariants(selector().all()) { variant ->
//		afterEvaluate {
//			val capName = variant.name.replaceFirstChar { it.uppercase() }
//			tasks.getByName<SourceTask>("ksp${capName}Kotlin") {
//				setSource(
//					tasks.getByName("generate${capName}AppDatabaseInterface").outputs
//				)
//			}
//		}
//	}
}

//afterEvaluate {
//	tasks.named("kspDebugKotlin").configure {
//		dependsOn(
//			"generateDebugProto"
//		)
//	}
//	tasks.named("kspReleaseKotlin").configure {
//		dependsOn("generateReleaseProto")
//	}
//}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:4.32.1"
	}
	generateProtoTasks {
		all().forEach { task ->
			task.builtins {
				create("java") {
					option("lite")
				}
				create("kotlin")
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
	implementation("androidx.core:core-ktx:1.18.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
	implementation("androidx.activity:activity-compose:1.13.0")
	implementation(platform("androidx.compose:compose-bom:2026.04.01"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material:material")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.compose.material:material-icons-extended")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.3.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
	androidTestImplementation(platform("androidx.compose:compose-bom:2026.04.01"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")

	implementation("androidx.navigation:navigation-compose:2.9.8")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
	implementation("androidx.compose.material3:material3-window-size-class")

	implementation("com.google.dagger:hilt-android:2.59.2")
	ksp("com.google.dagger:hilt-compiler:2.59.2")
//	ksp("androidx.hilt:hilt-compiler:1.3.0")
	implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

	implementation("androidx.datastore:datastore:1.2.1")
	implementation("com.google.protobuf:protobuf-kotlin-lite:4.34.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

	implementation("androidx.browser:browser:1.10.0")

	// Needed for:
	// java.time when targeting API Level <26
	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

	val ktorVersion = "3.4.3"
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
	implementation("io.ktor:ktor-client-auth:$ktorVersion")

	// Coil + BlurHash
	implementation(platform("io.coil-kt.coil3:coil-bom:3.4.0"))
	implementation("io.coil-kt.coil3:coil")
	implementation("io.coil-kt.coil3:coil-compose")
	implementation("io.coil-kt.coil3:coil-gif")
	implementation("io.coil-kt.coil3:coil-svg")
	implementation("io.coil-kt.coil3:coil-network-ktor3")
	implementation("io.coil-kt.coil3:coil-network-cache-control")
	testImplementation("io.coil-kt.coil3:coil-test")
	implementation("com.vanniktech:blurhash:0.3.0")
	implementation("com.github.penfeizhou.android.animation:apng:3.0.5")

	// SQLDelight
	implementation("app.cash.sqldelight:android-driver:2.3.2")
	implementation("app.cash.sqldelight:coroutines-extensions:2.3.2")
	implementation("com.google.code.gson:gson:2.14.0")

	// Firebase
	implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
	implementation("com.google.firebase:firebase-analytics")
	implementation("com.google.firebase:firebase-messaging")

	// Work Manager (used for firebase messaging)
	val work_version = "2.11.2"
	implementation("androidx.work:work-runtime-ktx:$work_version")
	implementation("androidx.hilt:hilt-work:1.3.0")

}
