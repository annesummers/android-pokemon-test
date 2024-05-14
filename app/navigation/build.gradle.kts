plugins {
    alias(commonLibs.plugins.android.library)
    alias(commonLibs.plugins.kotlin.android)
    alias(commonLibs.plugins.kotlin.linter)
    alias(commonLibs.plugins.kotlin.ksp)
    alias(pokemonLibs.plugins.hilt)
}

val javaVersion = JavaVersion.toVersion(commonLibs.versions.java.get())

val name = "navigation"

group = pokemonLibs.versions.library.group.get()
version = pokemonLibs.versions.library.version.get()

android {
    namespace = "$group.$name"
    compileSdk = commonLibs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = commonLibs.versions.android.min.get().toInt()
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
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }

    kotlinter {
        ignoreFailures = false
        reporters = arrayOf("checkstyle", "plain")
    }
}

dependencies {

    implementation(commonLibs.kotlinx.coroutines)
    implementation(androidLibs.kotlinx.coroutines.android)

    implementation(platform(androidLibs.compose.bom))
    implementation(androidLibs.bundles.compose)

    implementation(pokemonLibs.bundles.compose.navigation)

    ksp(pokemonLibs.hilt.compiler)
    implementation(pokemonLibs.hilt)

    debugImplementation(androidLibs.bundles.compose.debug)
    implementation(projects.android.navigation)
}