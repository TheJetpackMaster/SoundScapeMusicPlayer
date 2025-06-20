plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("kapt")

}

android {
    namespace = "com.SoundScapeApp.soundscape"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.SoundScapeApp.soundscape"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.3.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }



    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            proguardFiles ("proguard-rules.pro")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }


    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    val mediaVersion = "1.3.1"


//    MEDIA3
    implementation("androidx.media3:media3-common:$mediaVersion")
    implementation("androidx.media3:media3-session:$mediaVersion")
    implementation("androidx.media3:media3-exoplayer:$mediaVersion")
//    implementation("androidx.media3:media3-exoplayer-dash:$mediaVersion")
    implementation("androidx.media3:media3-ui:$mediaVersion")
//    implementation("androidx.media3:media3-exoplayer-workmanager:$mediaVersion")


//    Adaptive layouts
//    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.0.0-alpha07")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")

//    DAGGER-HILT
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.0")


    //NavigationCompose
    implementation("androidx.navigation:navigation-compose:2.8.0")


    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    //   GLIDE
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

//    COIL
    implementation("io.coil-kt:coil-compose:2.6.0")

//    PERMISSIONS MANAGER
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.media:media:1.7.0")

//    COLOR PICKER PALETTE
    implementation("androidx.palette:palette-ktx:1.0.0")

//    GSON
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.3.0")


    // SPLASH SCREEN API
    implementation("androidx.core:core-splashscreen:1.0.1")

    //Pager
    implementation("com.google.accompanist:accompanist-pager:0.18.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.18.0")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))

    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    // Firebase Messaging
    implementation("com.google.firebase:firebase-messaging")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}