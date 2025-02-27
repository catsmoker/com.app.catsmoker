// Suppress incubating API warnings
System.setProperty("org.gradle.warning.mode", "suppress")

pluginManagement {
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
        maven { url = uri("https://api.xposed.info") } // For Xposed API
        maven { url = uri("https://jitpack.io") } // For Shizuku and other dependencies
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/releases") }
    }
}

rootProject.name = "CATSMOKER"
include(":app")