plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.clipsync"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.clipsync"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
}
