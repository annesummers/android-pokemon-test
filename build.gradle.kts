// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(androidLibs.plugins.android.application) apply false
    alias(commonLibs.plugins.kotlin.android) apply false
    alias(commonLibs.plugins.android.library) apply false
    alias(commonLibs.plugins.kotlin.serialization) apply false
    alias(commonLibs.plugins.kotlin.linter) apply false
    alias(commonLibs.plugins.kotlin.ksp) apply false
    alias(commonLibs.plugins.kotlin.jvm) apply false
    alias(pokemonLibs.plugins.hilt) apply false
}