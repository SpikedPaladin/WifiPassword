plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "me.paladin.wifi"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.paladin.wifi"
        minSdk = 21
        targetSdk = 34
        versionCode = 8
        versionName = "1.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"

        // Disable ExperimentalMaterial3Api warning
        freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.zxing)
    implementation(libs.jsoup)

    // AdMob
    implementation(libs.admob)
    implementation(libs.ump)

    // Room
    implementation(libs.room)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.material)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Jetpack
    implementation(libs.core.ktx)
    implementation(libs.datastore)
    implementation(libs.lifecycle)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)

    constraints {
        implementation("androidx.fragment:fragment:1.6.2")
    }
}