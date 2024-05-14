
plugins {
    id("java-library")
    alias(commonLibs.plugins.kotlin.jvm)
}

val javaVersion = JavaVersion.toVersion(commonLibs.versions.java.get())

group = androidLibs.versions.library.group.get()
version = androidLibs.versions.library.version.get()

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}