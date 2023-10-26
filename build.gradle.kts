// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    ext {
        gradlePluginVersion = '8.1.2'
        kotlinVersion = '1.8.10'
    }
}
plugins {
    id 'com.android.application' version "${gradlePluginVersion}" apply false
    id 'com.android.library' version "${gradlePluginVersion}" apply false
    id 'org.jetbrains.kotlin.android' version "${kotlinVersion}" apply false
}