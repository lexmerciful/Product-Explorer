plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt.android.plugin)
    alias(libs.plugins.kapt)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.lexmerciful.productexplorer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lexmerciful.productexplorer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    //room
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    kapt (libs.androidx.room.compiler)
    implementation (libs.androidx.room.testing)

    //Viewmodel
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.extensions)

    //retrofit
    implementation(libs.retrofit)
    implementation (libs.converter.gson)

    // coroutine
    implementation (libs.kotlinx.coroutines.android)

    // material design
    implementation (libs.material.v1110)

    // navigation component
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

    // Coil
    implementation(libs.coil)

    // Swipe refresh layout
    implementation (libs.androidx.swiperefreshlayout)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Local Unit Tests
    implementation (libs.androidx.core)
    testImplementation (libs.junit)
    testImplementation (libs.androidx.core.testing)
    testImplementation (libs.kotlinx.coroutines.test)
    testImplementation (libs.truth)
    testImplementation (libs.mockito.core)
    testImplementation(libs.turbine)

    // Instrumented Test
    androidTestImplementation (libs.dexmaker.mockito)
    androidTestImplementation (libs.kotlinx.coroutines.test.v121)
    androidTestImplementation (libs.androidx.core.testing)
    androidTestImplementation (libs.truth)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
kapt {
    correctErrorTypes = true
}