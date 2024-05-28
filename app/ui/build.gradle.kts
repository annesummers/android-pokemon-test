plugins {
    alias(commonLibs.plugins.android.library)
    alias(commonLibs.plugins.kotlin.android)
    alias(commonLibs.plugins.kotlin.linter)
    alias(commonLibs.plugins.kotlin.ksp)
    alias(pokemonLibs.plugins.hilt)
}

val javaVersion = JavaVersion.toVersion(commonLibs.versions.java.get())

val name = "ui"

group = pokemonLibs.versions.library.group.get()
version = pokemonLibs.versions.library.version.get()

android {
    namespace = "$group.$name"
    compileSdk = commonLibs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = commonLibs.versions.android.min.get().toInt()

        testInstrumentationRunner = "com.giganticsheep.pokemon.ui.CustomTestRunner"
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
}

dependencies {

    implementation(projects.common.displayState)

    implementation(projects.android.ui)
    implementation(projects.android.navigation)

    implementation(projects.app.navigation)
    implementation(projects.app.common)
    implementation(projects.app.domain)

    implementation(commonLibs.kotlinx.coroutines)
    implementation(androidLibs.kotlinx.immutable)
    implementation(pokemonLibs.bundles.coil)

    implementation(platform(androidLibs.compose.bom))
    implementation(androidLibs.bundles.compose)

    debugImplementation(androidLibs.bundles.compose.debug)

    ksp(pokemonLibs.hilt.compiler)
    implementation(pokemonLibs.hilt)
    implementation(pokemonLibs.compose.navigation.hilt)

    testImplementation(commonLibs.bundles.test)
    testImplementation(commonLibs.test.junit)
    testImplementation(androidLibs.test.mockk.android)

    androidTestImplementation(platform(androidLibs.compose.bom))
    androidTestImplementation(androidLibs.compose.ui)
    androidTestImplementation(androidLibs.compose.test)

    androidTestImplementation(commonLibs.bundles.test)
    androidTestImplementation(androidLibs.bundles.test.instrumentation)
    androidTestImplementation(pokemonLibs.test.instrumentation.hilt)

    androidTestImplementation(commonLibs.test.assertK)
}