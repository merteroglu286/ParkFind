import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.merteroglu286.parkfind"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.merteroglu286.parkfind"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String","APPLICATION_ID", "\"$applicationId\"")

        /*
         val localPropertiesFile = project.rootProject.file("local.properties")
         val localProperties = Properties()
         localProperties.load(localPropertiesFile.inputStream())

         resValue(
             type = "string",
             name = "GOOGLE_MAP_KEY",
             value = localProperties.getProperty("google.maps.api.key").toString()
         )*/
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //dagger hilt
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)

    //room
    implementation (libs.room)
    ksp(libs.room.compiler)

    //coroutines
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)

    //maps
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.google.map.utils)
}