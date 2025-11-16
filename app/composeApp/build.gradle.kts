import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.roborazzi.plugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.compose.hotreload)
    alias(libs.plugins.buildkonfig)
}

val properties =
    Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.viewModelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.android)

            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.navigation)
            implementation(libs.kotlinx.serialization.json.okio)
            implementation(libs.okio)

            implementation(libs.ktorfit.lib)
            implementation(libs.bundles.ktor)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.annotations)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kermit)

            implementation(libs.coil.compose)
            implementation(libs.coil.svg)
            implementation(libs.image.loader)

            api(libs.datastore.preferences)
            api(libs.datastore)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.tdd.bookshelf"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.tdd.bookshelf"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        versionCode = project.properties["version_code"]?.toString()?.toInt() ?: 1
        versionName = project.properties["version"]?.toString() ?: "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

buildkonfig {
    packageName = "com.tdd.bookshelf"

    defaultConfigs {
        val baseUrl = properties.getProperty("BASE_URL")
        buildConfigField(Type.STRING, "BASE_URL", baseUrl)

        val aiUrl = properties.getProperty("AI_URL")
        buildConfigField(Type.STRING, "AI_URL", aiUrl)
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
}

tasks.named("runKtlintCheckOverCommonMainSourceSet") {
    dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
}

tasks.named("runKtlintFormatOverCommonMainSourceSet") {
    dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
}
