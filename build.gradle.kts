// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.test") version "8.12.0" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0" apply true
}


buildscript {
    dependencies {
        classpath( "com.google.dagger:hilt-android-gradle-plugin:2.56.1")
    }
}