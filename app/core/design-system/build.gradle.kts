plugins {
    id("tdd.android.feature")
    id("tdd.android.compose")
}

android {
    namespace = "com.tdd.design_system"
}

dependencies {
    implementation(projects.core)
}