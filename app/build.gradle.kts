import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "ru.netology.nework"
    compileSdk = 34

    buildFeatures.buildConfig = true
    buildFeatures.viewBinding = true

    defaultConfig {
        applicationId = "ru.netology.nework"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        val file = rootProject.file("nework.properties")
        if (file.exists()) {
            properties.load(file.inputStream())
        }
        buildConfigField("String", "API_KEY", properties.getProperty("API_KEY", ""))
        buildConfigField(
            "String",
            "MAPKIT_API_KEY",
            properties.getProperty("MAPKIT_API_KEY", "")
        )
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
}

kapt {
    correctErrorTypes = true
}

dependencies {
    val activityKtxVersion = "1.8.2"
    val fragmentVersion = "1.6.2"
    val navVersion = "2.7.7"
    val recyclerViewVersion = "1.3.2"
    val hiltVersion = "2.50"
    val gsonVersion = "2.10.1"
    val retrofitVersion = "2.9.0"
    val okhttpVersion = "4.12.0"
    val coroutineVersion = "1.7.3"
    val roomVersion = "2.6.1"
    val pagingVersion = "3.2.1"
    val swipeRefreshLayoutVersion = "1.2.0-alpha01"
    val imagePickerVersion = "2.1"
    val constraintLayoutVersion = "2.2.0-alpha13"
    val glideVersion = "4.16.0"
    val exoPlayerVersion = "1.2.1"
    val yaMapKitVersion = "4.4.0-lite"

    implementation("androidx.activity:activity-ktx:$activityKtxVersion")
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.recyclerview:recyclerview:$recyclerViewVersion")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:$swipeRefreshLayoutVersion")
    implementation("com.github.dhaval2404:imagepicker:$imagePickerVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    implementation("androidx.media3:media3-exoplayer:$exoPlayerVersion")
    implementation("androidx.media3:media3-exoplayer-dash:$exoPlayerVersion")
    implementation("androidx.media3:media3-ui:$exoPlayerVersion")
    implementation("com.yandex.android:maps.mobile:$yaMapKitVersion")


    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}