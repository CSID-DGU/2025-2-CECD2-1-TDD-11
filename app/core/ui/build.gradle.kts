plugins {
    id("tdd.android.feature")
    id("tdd.android.compose")
}

android {
    namespace = "com.tdd.ui"
}

dependencies {
    implementation(projects.core)
    implementation(projects.core.designSystem)
    implementation(projects.domain)
}