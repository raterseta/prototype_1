// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

buildscript{
    dependencies{
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.android.tools.build:gradle:8.1.1") // Pastikan versi Gradle yang sesuai
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0") // Kotlin plugin
    }
}

allprojects {
    repositories {
//        google()
//        mavenCentral()
    }
}

//build was configured to prefer settings repositories over project repositories