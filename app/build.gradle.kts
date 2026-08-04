plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.zuri.browser"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.zuri.browser"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "0.1.1-m1"

        vectorDrawables { useSupportLibrary = true }
    }

    // GeckoView ships native libraries per CPU architecture. Produce one APK per
    // ABI (arm64-v8a, armeabi-v7a) instead of a single universal APK bundling
    // every architecture — the main reason the first build was ~548 MB. x86/
    // x86_64 (emulator) ABIs are intentionally dropped.
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a")
            isUniversalApk = false
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            // GeckoView loads its own native libs (libxul, etc.) at runtime and
            // requires them to be extractable. Without this the engine renders a
            // blank page and the content process crashes on navigation. Legacy
            // (compressed) packaging also keeps the on-disk APK smaller.
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.datastore.preferences)

    // Firefox engine.
    implementation(libs.geckoview)

    debugImplementation(libs.androidx.ui.tooling)
}
