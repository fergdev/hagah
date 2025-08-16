@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id(libs.plugins.kotlinMultiplatform.id)
    id(libs.plugins.android.kotlin.multiplatform.library.id)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    explicitApi()
    androidLibrary {
        namespace = "com.fergdev.fcommon"
        compileSdk = 36
        minSdk = 21

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    wasmJs {
        outputModuleName = "fCommon"
        browser()
        binaries.library()
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(Config.jvmTarget)
            freeCompilerArgs.addAll(Config.jvmCompilerArgs)
        }
    }

    val xcfName = "fcommonKit"
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
        //noinspection WrongGradleMethod
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                api(compose.runtime)
                implementation(fLibs.kotlin.coroutines.core)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.kotlinx.datetime)
                implementation(compose.material3)
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(fLibs.kotlin.coroutines.android)
        }
//        iosMain .dependencies {
//            }
        desktopMain.dependencies {
            implementation(fLibs.kotlin.coroutines.swing)
        }
//        wasmJs {
//        }
    }
    sourceSets.apply {
        all {
            languageSettings {
                progressiveMode = true
                //noinspection WrongGradleMethod
                Config.optIns.forEach { optIn(it) }
            }
        }
    }
}
