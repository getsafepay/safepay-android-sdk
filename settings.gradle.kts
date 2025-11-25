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
        maven { url  = uri("https://cardinalcommerceprod.jfrog.io/artifactory/android")
            credentials {
                username =  "hzaidi@getsafepay.com"
                password ="cmVmdGtuOjAxOjE3NTU5NDg3Njc6c0RzUW1pUVVMYUpBa01uNDFzelBZUWdScWtF"
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
