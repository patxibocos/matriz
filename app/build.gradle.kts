plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "com.patxi.intellijsplashscreen"
        minSdk = 21
        targetSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
}

dependencies {
    implementation(libs.android.material)
    implementation(libs.compose.material.material)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.ui)
    implementation(libs.activity.compose)

    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.runner)
}

tasks.withType<Test> {
    useJUnitPlatform()
}