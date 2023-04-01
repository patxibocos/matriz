import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.diffplug.spotless") version ("6.17.0")
    `maven-publish`
    signing
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
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
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
}

dependencies {
    implementation(libs.compose.foundation.foundation)

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

tasks.register<Jar>(name = "androidSourcesJar") {
    val androidSourceSet =
        android.sourceSets["main"].java.srcDirs() as DefaultAndroidSourceDirectorySet
    from(androidSourceSet.srcDirs)
    archiveClassifier.set("sources")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "io.github.patxibocos"
                artifactId = "matriz"
                version = project.version.toString()
                description = "A grid canvas composable for Android Jetpack Compose"

                from(components["release"])
                artifact(tasks["androidSourcesJar"])

                pom {
                    name.set("Matriz")
                    description.set("A grid canvas composable for Android Jetpack Compose")
                    url.set("https://github.com/patxibocos/matriz")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/mit-license.php")
                        }
                    }
                    developers {
                        developer {
                            id.set("patxibocos")
                            name.set("Patxi Bocos")
                            email.set("patxi.bocos.vidal@gmail.com")
                            url.set("https://twitter.com/patxibocos")
                            timezone.set("Europe/Madrid")
                        }
                    }
                    scm {
                        connection.set("scm:git:github.com/patxibocos/matriz.git")
                        developerConnection.set("scm:git:ssh://github.com/patxibocos/matriz.git")
                        url.set("https://github.com/patxibocos/matriz/tree/main")
                    }
                }
            }
        }
    }

    signing {
        val signingKeyId: String? by project
        val signingSecretKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKeyId, signingSecretKey, signingPassword)
        sign(publishing.publications)
    }
}