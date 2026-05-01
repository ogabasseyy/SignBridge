plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.signbridge"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.signbridge"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.mlkit.genai.prompt)
    implementation(libs.mlkit.genai.speech.recognition)

    testImplementation(libs.junit)

    // Phase 0 fallback only. Enable if ML Kit Prompt API cannot access Gemma on S24 Ultra.
    // implementation(libs.litertlm.android)
}
