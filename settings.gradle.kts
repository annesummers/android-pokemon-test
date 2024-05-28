pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("commonLibs") {
            from(files("./gradle/common.versions.toml"))
        }
        create("androidLibs") {
            from(files("./gradle/android.versions.toml"))
        }
        create("pokemonLibs") {
            from(files("./gradle/pokemon.versions.toml"))
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Pokemon"
include(":app")
include(":app:domain")
include(":app:data")
include(":app:ui")
include(":android")
include(":android:ui")
include(":common")
include(":common:network")
include(":common:response")
include(":common:logging")
include(":app:common")
include(":android:navigation")
include(":app:navigation")
include(":app:display")
include(":common:displayState")
