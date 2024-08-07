plugins {
    alias(libs.plugins.nexus.publish)
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.plugin.gradle)
        classpath(libs.kotlin.plugin.gradle)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

nexusPublishing {
    this.repositories {
        sonatype {
            val sonatypeStagingProfileId: String? by project
            val sonatypeUsername: String? by project
            val sonatypePassword: String? by project
            stagingProfileId.set(sonatypeStagingProfileId)
            username.set(sonatypeUsername)
            password.set(sonatypePassword)
            // only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
