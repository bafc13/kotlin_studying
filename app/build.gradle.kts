import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android.buildFeatures.buildConfig = true
val localPropsFile = project.rootProject.file("local.properties")
val yandexMapKitApiKey: String = if (localPropsFile.exists()) {
    val properties = Properties()
    properties.load(localPropsFile.inputStream())
    properties.getProperty("yandexMapKitApiKey") ?: ""
} else {
    ""
}

android {
    namespace = "com.example.lab1_bafc13"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.lab1_bafc13"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField ("String", "MAPKIT_API_KEY", "\"$yandexMapKitApiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.google.android.material:material:1.13.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.yandex.android:maps.mobile:4.26.0-lite")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
