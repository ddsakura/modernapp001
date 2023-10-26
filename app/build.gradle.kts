plugins {
    id 'com.android.application'
    id 'kotlin-android'
}

android {

    buildFeatures {
        // for view binding, https://developer.android.com/topic/libraries/view-binding
        viewBinding true
    }

    defaultConfig {
        applicationId "cc.ddsakura.modernapp001"
        minSdk 21
        // https://codelabs.developers.google.com/handling-gesture-back-navigation#4
        compileSdk 34
        targetSdk 33
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        // https://developer.android.com/studio/write/java8-support
        coreLibraryDesugaringEnabled true
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        compose true
    }
    composeOptions {
        kotlinCompilerExtensionVersion "1.4.3"
    }
    namespace 'cc.ddsakura.modernapp001'
}

dependencies {
    // https://developer.android.com/studio/write/java8-support
    coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:2.0.3'
    implementation 'androidx.webkit:webkit:1.8.0'
    implementation 'androidx.core:core-ktx:1.12.0'
    // implementation 'androidx.appcompat:appcompat:1.4.2'
    implementation 'androidx.appcompat:appcompat:1.7.0-alpha03'
    implementation 'androidx.fragment:fragment-ktx:1.6.1'
    implementation 'com.google.android.material:material:1.10.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'

    // Compose
    implementation 'androidx.activity:activity-compose:1.8.0'
    implementation "androidx.navigation:navigation-compose:2.7.4"
    def composeBom = platform('androidx.compose:compose-bom:2023.10.00')
    implementation composeBom
    androidTestImplementation composeBom
    implementation 'androidx.compose.ui:ui'
    // Material Design 2
    implementation 'androidx.compose.material:material'
    // Optional - Add full set of material icons
    implementation 'androidx.compose.material:material-icons-extended'
    // Android Studio Preview support
    implementation 'androidx.compose.ui:ui-tooling-preview'
    debugImplementation 'androidx.compose.ui:ui-tooling'

    // AppWidget Compose
    implementation "androidx.glance:glance-appwidget:1.0.0"

    implementation "androidx.navigation:navigation-fragment-ktx:2.7.4"
    implementation "androidx.navigation:navigation-ui-ktx:2.7.4"
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}