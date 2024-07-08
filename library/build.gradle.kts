plugins {
    id("com.android.library")
    id("kotlin-android")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.spotless)
    `maven-publish`
    signing
}

android {
    namespace = "io.github.patxibocos.matriz"
    compileSdk = 34

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

    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
    }
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
