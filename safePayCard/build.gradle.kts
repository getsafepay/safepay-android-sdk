plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

// Card field + card capture controller. Must not depend on the Cardinal Mobile SDK, so hosts that
// only capture cards (e.g. the React Native SDK) build without Cardinal Maven credentials.
android {
    namespace = "com.android.safepay.card"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Pinned coordinates instead of the version catalog: host apps include this module directly and
    // don't have this repository's catalog.
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
