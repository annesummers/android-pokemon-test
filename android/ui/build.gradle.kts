plugins {
    alias(commonLibs.plugins.android.library)
    alias(commonLibs.plugins.kotlin.android)
    alias(commonLibs.plugins.kotlin.linter)
}

val javaVersion = JavaVersion.toVersion(commonLibs.versions.java.get())

val name = "ui"

group = androidLibs.versions.library.group.get()
version = androidLibs.versions.library.version.get()

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

    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = androidLibs.versions.compose.compiler.get()
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

    api(projects.common.response)

    implementation(commonLibs.kotlinx.coroutines)
    implementation(androidLibs.kotlinx.coroutines.android)

    implementation(platform(androidLibs.compose.bom))
    implementation(androidLibs.bundles.compose)

    debugImplementation(androidLibs.bundles.compose.debug)

    androidTestImplementation(platform(androidLibs.compose.bom))
    androidTestImplementation(androidLibs.compose.ui)
    androidTestImplementation(androidLibs.compose.test)
/*
    androidTestImplementation(commonLibs.bundles.test)
    androidTestImplementation(androidLibs.bundles.test.instrumentation)*/

    //androidTestImplementation(libs.test.assertK)

    testImplementation(commonLibs.bundles.test)
    //testImplementation(libs.test.assertK)
}