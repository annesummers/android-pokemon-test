
plugins {
    id("java-library")
    alias(commonLibs.plugins.kotlin.jvm)
}

group = commonLibs.versions.library.group.get() + "common"
version = commonLibs.versions.library.version.get()

val javaVersion = JavaVersion.toVersion(commonLibs.versions.java.get())

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
