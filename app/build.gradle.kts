import java.io.FileInputStream
import java.util.Properties

plugins {
    id(BuildPlugins.ANDROID_APPLICATION) version PluginVersions.AGP
    id(BuildPlugins.KOTLIN_ANDROID) version PluginVersions.KOTLIN
    id(BuildPlugins.KOTLIN_KSP) version PluginVersions.KSP
    id(BuildPlugins.HILT) version PluginVersions.HILT
    id(BuildPlugins.NAVIGATION_SAFEARGS) version PluginVersions.NAVIGATION_SAFEARGS
//    id(BuildPlugins.SECRETS_GRADLE_PLUGIN) version PluginVersions.SECRETS_GRADLE_PLUGIN
}

android {
    namespace = BuildConfig.APP_ID
    compileSdk = BuildConfig.COMPILE_SDK_VERSION

    val localPropertiesFile = project.rootProject.file("local.properties")
    val localProperties = Properties()
    localProperties.load(localPropertiesFile.inputStream())

    defaultConfig {
        applicationId = BuildConfig.APP_ID
        minSdk = BuildConfig.MIN_SDK_VERSION
        targetSdk = BuildConfig.TARGET_SDK_VERSION
        versionCode = ReleaseConfig.VERSION_CODE
        versionName = ReleaseConfig.VERSION_NAME

        testInstrumentationRunner = TestBuildConfig.TEST_INSTRUMENTATION_RUNNER

        buildConfigField("String","APPLICATION_ID", "\"$applicationId\"")


        resValue(
            type = "string",
            name = "GOOGLE_MAP_KEY",
            value = localProperties.getProperty("google.maps.api.key").toString()
        )
    }

    signingConfigs {
        create("release") {
            val myKeystorePath = localProperties.getProperty("keystore.path") ?: ""
            val myKeystorePassword = localProperties.getProperty("keystore.password") ?: ""
            val myKeyAlias = localProperties.getProperty("key.alias") ?: ""
            val myKeyPassword = localProperties.getProperty("key.password") ?: ""

            storeFile = myKeystorePath.let { if (it.isNotEmpty()) file(it) else null }
            storePassword = myKeystorePassword
            keyAlias = myKeyAlias
            keyPassword = myKeyPassword
        }
    }

    buildTypes {
        getByName(BuildTypes.RELEASE) {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isMinifyEnabled = Build.Release.isMinifyEnabled
            enableUnitTestCoverage = Build.Release.enableUnitTestCoverage
            isDebuggable = Build.Release.isDebuggable
            signingConfig = signingConfigs.getByName("release")
        }

        getByName(BuildTypes.DEBUG) {
            applicationIdSuffix = Build.Debug.applicationIdSuffix
            versionNameSuffix = Build.Debug.versionNameSuffix
            isMinifyEnabled = Build.Debug.isMinifyEnabled
            enableUnitTestCoverage = Build.Debug.enableUnitTestCoverage
            isDebuggable = Build.Debug.isDebuggable
        }

        create(BuildTypes.RELEASE_EXTERNAL_QA) {
            applicationIdSuffix = Build.ReleaseExternalQa.applicationIdSuffix
            versionNameSuffix = Build.ReleaseExternalQa.versionNameSuffix
            isMinifyEnabled = Build.ReleaseExternalQa.isMinifyEnabled
            enableUnitTestCoverage = Build.ReleaseExternalQa.enableUnitTestCoverage
            isDebuggable = Build.ReleaseExternalQa.isDebuggable
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

    implementation(Dependencies.ANDROIDX_CORE_KTX)
    implementation(Dependencies.ANDROIDX_APPCOMPAT)
    implementation(Dependencies.MATERIAL)
    implementation(Dependencies.CONSTRAINTLAYOUT)

    testImplementation(TestDependencies.JUNIT)
    androidTestImplementation(TestDependencies.ANDROIDX_JUNIT)
    androidTestImplementation(TestDependencies.ANDROIDX_ESPRESSO_CORE)

    implementation(Dependencies.NAVIGATION_FRAGMENT_KTX)
    implementation(Dependencies.NAVIGATION_UI_KTX)

    implementation(Dependencies.HILT_ANDROID)
    ksp(Dependencies.HILT_COMPILER)

    implementation(Dependencies.ROOM)
    implementation(Dependencies.ROOM_KTX)
    ksp(Dependencies.ROOM_COMPILER)

    implementation(Dependencies.COROUTINES_ANDROID)
    implementation(Dependencies.COROUTINES_CORE)

    implementation(Dependencies.PLAY_SERVICES_MAPS)
    implementation(Dependencies.PLAY_SERVICES_LOCATION)
    implementation(Dependencies.GOOGLE_MAP_UTIL)

    implementation(Dependencies.INDICATOR)

}