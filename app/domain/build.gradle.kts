plugins {
    alias(commonLibs.plugins.android.library)
    alias(commonLibs.plugins.kotlin.android)
    alias(commonLibs.plugins.kotlin.linter)
    alias(commonLibs.plugins.kotlin.ksp)
    alias(pokemonLibs.plugins.hilt)
}

val javaVersion = JavaVersion.toVersion(commonLibs.versions.java.get())

val name = "data"

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

    flavorDimensions += "environment"
    productFlavors {
        create("offline") {
            dimension = "environment"
        }
        create("online") {
            dimension = "environment"
        }
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
    implementation(projects.app.data)
    implementation(projects.app.common)

    implementation(projects.android.ui)

    implementation(projects.common.response)

    implementation(androidLibs.kotlinx.immutable)

    implementation(platform(androidLibs.compose.bom))
    implementation(androidLibs.compose.runtime)

    ksp(pokemonLibs.hilt.compiler)
    implementation(pokemonLibs.hilt)

    testImplementation(commonLibs.bundles.test)
    testImplementation(commonLibs.test.junit)
    testImplementation(androidLibs.test.mockk.android)

}