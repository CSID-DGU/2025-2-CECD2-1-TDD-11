import java.util.Properties

plugins {
    id("tdd.android.application")
    id("tdd.android.hilt")
    id("tdd.android.kotlin")
    id("tdd.retrofit")
    id("com.google.gms.google-services")
}

val properties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.tdd.bookshelf"

    defaultConfig {
        val baseUrl = properties.getProperty("BASE_URL")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }
}

dependencies {
    implementation(projects.feature)
    implementation(projects.domain)
    implementation(projects.core)
    implementation(projects.core.firebase)
    implementation(projects.data)

    implementation(libs.gson)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.okhttp.urlconnection)

    // FCM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
}