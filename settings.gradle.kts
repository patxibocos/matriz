dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://kotlin.bintray.com/kotlinx")
    }
}
rootProject.name = "Matriz"
include(":app")

enableFeaturePreview("VERSION_CATALOGS")