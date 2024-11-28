plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "cc.ddsakura.modernapp001"
    compileSdk = 35

    defaultConfig {
        applicationId = "cc.ddsakura.modernapp001"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.webkit)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.fragment.ktx)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)

    implementation(libs.retrofit)

    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.glance.appwidget)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    debugImplementation(libs.androidx.glance.preview)
    debugImplementation(libs.androidx.glance.appwidget.preview)
    debugImplementation(libs.androidx.ui.test.manifest)
}
