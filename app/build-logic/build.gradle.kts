plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.agp)
    implementation(libs.kotlin.gradleplugin)
    compileOnly(libs.compose.compiler.extension)
}

gradlePlugin {
    plugins {
        register("android-application") {
            id = "tdd.android.application"
            implementationClass = "com.tdd.plugin.AndroidApplicationPlugin"
        }
        register("android-compose") {
            id = "tdd.android.compose"
            implementationClass = "com.tdd.plugin.AndroidComposePlugin"
        }
        register("android-feature") {
            id = "tdd.android.feature"
            implementationClass = "com.tdd.plugin.AndroidFeaturePlugin"
        }
        register("android-hilt") {
            id = "tdd.android.hilt"
            implementationClass = "com.tdd.plugin.AndroidHiltPlugin"
        }
        register("android-kotlin") {
            id = "tdd.android.kotlin"
            implementationClass = "com.tdd.plugin.AndroidKotlinPlugin"
        }
        register("kotlin-jvm") {
            id = "tdd.kotlin.jvm"
            implementationClass = "com.tdd.plugin.KotlinJvmPlugin"
        }
        register("kotlin-serialization") {
            id = "tdd.kotlin.serialization"
            implementationClass = "com.tdd.plugin.KotlinSerializationPlugin"
        }
        register("retrofit") {
            id = "tdd.retrofit"
            implementationClass = "com.tdd.plugin.RetrofitPlugin"
        }
    }
}