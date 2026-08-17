plugins {
    id("com.android.application")
}

android {
    namespace = "com.vidora.ai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.vidora.ai"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "2.0.0"
        buildConfigField("String", "VIDORA_API_BASE_URL", "\"https://api.example.com/vidora/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        buildConfig = true
    }
}
