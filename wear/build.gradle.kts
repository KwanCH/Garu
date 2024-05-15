plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" // Use your Kotlin version


    /*    //Hilt
        kotlin("kapt")
        id("com.google.dagger.hilt.android")*/
}

android {
    namespace = "com.example.ak24project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ak24project"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

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

    wearApp(project(":wear"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0") // Use the latest version

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))

    //Compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.1")

    //wear dependencies
    implementation("androidx.wear.compose:compose-material:1.3.0")
    implementation("androidx.wear.compose:compose-foundation:1.3.0")
    implementation("androidx.wear:wear-ongoing:1.0.0")

    //Health Service
    implementation("androidx.health:health-services-client:1.1.0-alpha02")
    implementation("com.google.guava:guava:30.1-android")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0") // Check for the latest version
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3") // Check for the latest version

/*        //Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-android-compiler:2.51")*/



    //Implements fragment navigation control
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    //Testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// Allow references to generated code
/*
kapt {
    correctErrorTypes = true
}*/
