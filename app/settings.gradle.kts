enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "bookshelf"
include(":app")

include(":core")
include(":core:ui")
include(":core:navigation")
include(":core:design-system")
include(":core:firebase")

include(":data")
include(":domain")

include(":feature")
include(":feature:onboarding")
include(":feature:interview")
include(":feature:interviewchapter")
include(":feature:progress")