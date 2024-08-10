plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("maven-publish")
}

android {
    namespace = "com.lib.admoblib"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.review.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.play.services.ads)
    implementation (libs.shimmer)
    implementation (libs.androidx.multidex)
    implementation ("com.github.ybq:Android-SpinKit:1.4.0")
    implementation(libs.user.messaging.platform)
    implementation(libs.app.update)
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.0")
}


publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.kashifali23"
            artifactId = "AdmobeAdsLibrary"
            version = "1.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

