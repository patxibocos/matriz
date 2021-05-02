plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.diffplug.spotless") version ("5.12.1")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 21
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
    implementation(libs.compose.material.material)
    implementation(libs.compose.ui.tooling)

    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.runner)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint(libs.versions.ktlint.get())
    }
}