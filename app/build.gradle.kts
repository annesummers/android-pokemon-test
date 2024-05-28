plugins {
    alias(androidLibs.plugins.android.application)
    alias(commonLibs.plugins.kotlin.android)
    alias(commonLibs.plugins.kotlin.linter)
    alias(commonLibs.plugins.kotlin.ksp)
    alias(pokemonLibs.plugins.hilt)
}

val javaVersion = JavaVersion.toVersion(commonLibs.versions.java.get())

val name = "app"

group = pokemonLibs.versions.library.group.get()
version = pokemonLibs.versions.library.version.get()

android {
    namespace = "$group.$name"
    compileSdk = commonLibs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = commonLibs.versions.android.min.get().toInt()
        targetSdk = commonLibs.versions.android.target.get().toInt()

        applicationId = "com.giganticsheep.pokemon"

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.giganticsheep.ui.CustomTestRunner"

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
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = androidLibs.versions.compose.compiler.get()
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.app.common)
    implementation(projects.app.navigation)
    implementation(projects.app.ui)
    implementation(projects.app.display)

    implementation(projects.android.navigation)
    implementation(projects.android.ui)

    implementation(commonLibs.kotlinx.coroutines)

    implementation(platform(androidLibs.compose.bom))

    implementation(androidLibs.bundles.compose)
    implementation(pokemonLibs.bundles.compose.navigation)

    debugImplementation(androidLibs.bundles.compose.debug)

    ksp(pokemonLibs.hilt.compiler)
    implementation(pokemonLibs.hilt)

    testImplementation(commonLibs.bundles.test)
    testImplementation(commonLibs.test.junit)
    testImplementation(androidLibs.test.mockk.android)
}