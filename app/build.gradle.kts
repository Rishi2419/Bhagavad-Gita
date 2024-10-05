plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.bhagwatgita"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.riya.bhagwatgita"
        minSdk = 26
        targetSdk = 34
        versionCode = 9
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

//    signingConfigs {
//        create("release") {
//            storeFile = file(project.property("KEYSTORE_FILE").toString())
//            storePassword = project.property("KEYSTORE_PASSWORD").toString()
//            keyAlias = project.property("SIGNING_KEY_ALIAS").toString()
//            keyPassword = project.property("SIGNING_KEY_PASSWORD").toString()
//        }
//    }
    buildTypes {
        release {
//            getByName("release") {
//                // your configuration here
//                signingConfig = signingConfigs.getByName(name)
//            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
//    signingConfigs {
//        create("release") {
//            storeFile = file(project.property("KEYSTORE_FILE").toString())
//            storePassword = project.property("KEYSTORE_PASSWORD").toString()
//            keyAlias = project.property("SIGNING_KEY_ALIAS").toString()
//            keyPassword = project.property("SIGNING_KEY_PASSWORD").toString()
//        }
//    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    //text dimension
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    //lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

//shimmer effect
    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")

    //room database   kapt is imp other wise one crash indicating unable to create instance of class
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //bottom navigation
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7")

    // shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //ballon

    implementation("com.github.skydoves:balloon:1.6.4")

    //read more

    // work manager
    implementation("androidx.work:work-runtime:2.7.0")


}