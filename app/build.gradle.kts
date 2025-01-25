plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.van.imu"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.van.imu"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        create("keyStore") {
            keyAlias = "platform"
            keyPassword = "android"
            storeFile = file("Qualcomm_9.jks")
            storePassword = "android"
        }
    }

    buildTypes {
        val signConfig = signingConfigs.getByName("keyStore")

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signConfig

            android.applicationVariants.all {
                val buildType = this.buildType.name
                outputs.all {
                    // 判断是否是输出 apk 类型
                    if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                        this.outputFileName = "VanImu_v${defaultConfig.versionName}_$buildType.apk"
                    }
                }
            }
        }

        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signConfig
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets["main"].jniLibs.srcDir("libs")
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
}