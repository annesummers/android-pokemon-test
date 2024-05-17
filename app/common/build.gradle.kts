plugins {
    alias(commonLibs.plugins.android.library)
    alias(commonLibs.plugins.kotlin.android)
    alias(commonLibs.plugins.kotlin.linter)
    alias(commonLibs.plugins.kotlin.ksp)
    alias(commonLibs.plugins.kotlin.serialization)
    alias(pokemonLibs.plugins.hilt)
}

val javaVersion = JavaVersion.toVersion(commonLibs.versions.java.get())

val name = "common"

group = commonLibs.versions.library.group.get()
version = commonLibs.versions.library.version.get()

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
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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

    ksp(pokemonLibs.hilt.compiler)
    implementation(pokemonLibs.hilt)

    testImplementation(commonLibs.bundles.test)
    testImplementation(commonLibs.test.assertK)
}