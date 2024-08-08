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
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    kotlinOptions {
        jvmTarget = "1.8"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.google.android.gms:play-services-ads:23.1.0")
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("androidx.multidex:multidex:2.0.1")
    implementation ("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("com.google.android.ump:user-messaging-platform:3.0.0")
    implementation("com.google.android.play:app-update:2.1.0")

}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
                groupId = "com.github.kashali98"
                artifactId = "AdmobeAdsLibrary"
                version = "1.0.3"
            }
        }
    }
}

//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("maven") {
//                from(components["release"])
//                groupId = "com.github.kashali98"
//                artifactId = "libraryads"
//                version = "1.0.0"
//                // Ensure attributes for Java version and elements
//                pom {
//                    withXml {
//                        asNode().appendNode("dependencies").appendNode("dependency").apply {
//                            appendNode("groupId", "org.gradle")
//                            appendNode("artifactId", "gradle-api")
//                            appendNode("version", "8.4")
//                        }
//                    }
//                }
//            }
//        }
//        repositories {
//            mavenLocal() // Optional for local testing
//        }
//    }
//}
