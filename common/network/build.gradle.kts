plugins {
    alias(commonLibs.plugins.android.library)
    alias(commonLibs.plugins.kotlin.android)
    alias(commonLibs.plugins.kotlin.linter)
    alias(commonLibs.plugins.kotlin.serialization)
}

val name = "network"

group = commonLibs.versions.library.group.get()
version = commonLibs.versions.library.version.get()

val javaVersion = JavaVersion.toVersion(commonLibs.versions.java.get())

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

    implementation(projects.common.response)
    implementation(projects.common.logging)

    implementation(commonLibs.kotlinx.coroutines)
    implementation(commonLibs.kotlinx.serialization.json)

    implementation(commonLibs.bundles.ktor)

    testImplementation(commonLibs.bundles.test)
}