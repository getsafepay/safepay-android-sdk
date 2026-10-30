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
        maven { url = uri("https://jitpack.io") }
        // Cardinal Mobile SDK credentials: set CARDINAL_USERNAME / CARDINAL_PASSWORD in
        // ~/.gradle/gradle.properties or the environment. Never commit them; this repo is public.
        maven {
            url = uri("https://cardinalcommerceprod.jfrog.io/artifactory/android")
            credentials {
                username = providers.gradleProperty("CARDINAL_USERNAME")
                    .orElse(providers.environmentVariable("CARDINAL_USERNAME")).getOrElse("")
                password = providers.gradleProperty("CARDINAL_PASSWORD")
                    .orElse(providers.environmentVariable("CARDINAL_PASSWORD")).getOrElse("")
            }
        }

        flatDir {
            dirs("libs")
        }
    }
}

rootProject.name = "SafePay"
include(":app")
include(":safePay")
