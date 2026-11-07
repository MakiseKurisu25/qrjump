plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // 包名刻意不用 com.qiekj.*：避免被误认为厂商官方应用，也不碰任何厂商命名空间。
    namespace = "com.local.qrjump"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.local.qrjump"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        // 不声明任何敏感权限，只有 CAMERA 与 INTERNET
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
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
        viewBinding = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // CameraX
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // 主解码器：ML Kit（GMS 版）。国行机若没有 GMS，运行时自动降级到 ZXing。
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // 备选解码器：ZXing core，纯本地、无 GMS 依赖，只用来兜底扫二维码。
    implementation("com.google.zxing:core:3.5.3")
}
