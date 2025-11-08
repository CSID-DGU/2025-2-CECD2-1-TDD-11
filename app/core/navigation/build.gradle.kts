plugins {
    id("tdd.android.feature")
    id("tdd.android.compose")
}

android {
    namespace = "com.tdd.navigation"
}

dependencies {
    implementation(projects.core)
    implementation(projects.domain)

    implementation(projects.feature.onboarding)
    implementation(projects.feature.interview)
    implementation(projects.feature.interviewchapter)
    implementation(projects.feature.progress)
}