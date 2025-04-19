@file:Suppress("UnstableApiUsage")

rootProject.name = "Hagah"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
    }
    versionCatalogs {
        create("fLibs") {
            from(files("fcommon/libs.versions.toml"))
        }
        create("serverLibs") {
            from(files("server/libs.versions.toml"))
        }
    }
}

include(":composeApp")
include(":fcommon")
include(":server")
include(":data")
