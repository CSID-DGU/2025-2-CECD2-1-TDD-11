plugins {
    id("tdd.android.feature")
    id("tdd.android.compose")
}

android {
    namespace = "com.tdd.achieve"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core)
    implementation(projects.core.ui)
    implementation(projects.core.designSystem)
}