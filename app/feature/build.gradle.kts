plugins {
    id("tdd.android.feature")
    id("tdd.android.compose")
    id("tdd.android.hilt")
    id("tdd.android.kotlin")
}

android {
    namespace = "com.tdd.feature"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core)
    implementation(projects.core.ui)
    implementation(projects.core.designSystem)
    implementation(projects.core.navigation)
}