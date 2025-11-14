plugins {
    id("tdd.android.feature")
    id("tdd.retrofit")
    id("tdd.android.hilt")
}

android {
    namespace = "com.tdd.data"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core)
    implementation(projects.core.firebase)

    implementation(libs.androidx.datastore)
    implementation(libs.gson)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.okhttp.urlconnection)
}