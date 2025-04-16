import org.intellij.lang.annotations.Language
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id(libs.plugins.kotlinMultiplatform.id)
    id(libs.plugins.androidApplication.id)
    alias(libs.plugins.androidJunit5)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotest)
}

val isDebug = project.hasProperty("isDebug") || gradle.startParameter.taskNames.any { it.contains("Debug") }
val baseUrl = if (isDebug) "http://10.0.2.2:8080" else "https://api.example.com"

@Language("Kotlin")
// language=kotlin
val buildConfig = """
    package ${Config.namespace}
    internal object BuildFlags {
        const val appName = "${Config.appName}"
        const val versionName = "${Config.versionName}"
        const val privacyPolicyUrl = "${Config.privacyPolicyUrl}"
        const val supportEmail = "${Config.supportEmail}"
        const val apiKey = "${localProperties().value.openApiKey()}"
        const val mockData = "${localProperties().value.mockData()}"
        const val baseUrl = "${baseUrl}"
    }
""".trimIndent()

val generateBuildConfig by tasks.registering(Sync::class) {
    from(resources.text.fromString(buildConfig)) {
        rename { "BuildFlags.kt" }
        into(Config.namespace.replace(".", "/"))
    }
    // the target directory
    into(layout.buildDirectory.dir("generated/kotlin/src/commonMain"))
}

kotlin {
    applyDefaultHierarchyTemplate()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
                export = true
            }
        }
        binaries.executable()
    }

    jvm("desktop").compilations.all {
        compileTaskProvider.configure {
            compilerOptions {
                freeCompilerArgs.addAll(Config.jvmCompilerArgs)
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
        //noinspection WrongGradleMethod
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
        iosTarget.compilerOptions {
            freeCompilerArgs.addAll(Config.compilerArgs)
            freeCompilerArgs.addAll("-Xbinary=bundleId=ComposeApp")
        }
    }

    androidTarget().compilations.all {
        compileTaskProvider.configure {
            compilerOptions {
                jvmTarget = Config.jvmTarget
                freeCompilerArgs.addAll(Config.jvmCompilerArgs)
            }
        }
    }

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll(Config.compilerArgs)
                    optIn.addAll(Config.appOptIns)
                    progressiveMode.set(true)
                }
            }
        }
    }

    sourceSets {
        val desktopMain by getting
        val desktopTest by getting
        commonMain {
            compilerOptions {
                freeCompilerArgs.addAll(Config.compilerArgs)
                optIn.addAll(Config.appOptIns)
                progressiveMode.set(true)
            }
            kotlin.srcDir(generateBuildConfig.map { it.destinationDir })
            dependencies {
                implementation(projects.fcommon)
                implementation(projects.data)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)

                implementation(libs.aboutLibs)

                implementation(libs.koin.core)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.navigation.compose)
                implementation(libs.napier)
                implementation(libs.compottie)

                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.coroutines)
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.multiplatform.settings.observable)

                implementation(libs.arrow.core)
                implementation(libs.arrow.fx.coroutines)

                implementation(libs.kstore)

                implementation(libs.kotlinx.datetime)
                implementation(libs.coil.compose)
                implementation(libs.coil.svg)
                implementation(libs.slf4j.nop)
            }
        }
        commonTest.dependencies {
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.koin.test)
            implementation(libs.kotest)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotlin.coroutines.test)
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.multiplatform.settings.test)
            implementation(libs.turbine)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.foundation.layout.android)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ui)
            implementation(libs.koin.android)
            implementation(libs.kstore.file)
            implementation(libs.ktor.client.okhttp)
        }
        androidUnitTest.dependencies {
            implementation(libs.junit)
            implementation(libs.junit.api)
            implementation(libs.junit.engine)
            implementation(libs.kotest.junit)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.kstore.file)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.apache)
            implementation(libs.appdirs)
            implementation(libs.kstore.file)
            implementation(libs.composemediaplayer)
        }
        desktopTest.dependencies {
            implementation(libs.kotest.junit)
            implementation(libs.koin.test)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.kstore.storage)
            implementation(libs.composemediaplayer)
            implementation(libs.kotlinx.browser.wasm.js)
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

android {
    namespace = Config.namespace
    buildFeatures {
        buildConfig = true
        resValues = true
    }
    defaultConfig {
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        compileSdk = Config.compileSdk

        versionCode = Config.versionCode
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"
    }
    compileOptions {
        sourceCompatibility = Config.javaVersion
        targetCompatibility = Config.javaVersion
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            with(localProperties().value) {
                storePassword = storePassword()
                storeFile = File(rootDir, keyStorePath())
                keyPassword = keyPassword()
                keyAlias = keyAlias()
            }
        }
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
}

dependencies {
    debugImplementation(libs.androidx.compose.ui.tooling)
}

compose {
    resources {
        packageOfResClass = Config.packageOfResClass
        publicResClass = true
    }
    android {}
    web { }
    desktop {
        application {
            buildTypes.release.proguard {
                version.set("7.7.0") // TODO test this for other builds
                obfuscate = true
                optimize = true
                configurationFiles.from(projectDir.resolve("proguard-rules.pro"))
            }
            mainClass = Config.mainClass
            nativeDistributions {
                targetFormats(
                    TargetFormat.Deb,
                    TargetFormat.Rpm,
                    TargetFormat.Dmg,
                    TargetFormat.Pkg,
                    TargetFormat.Msi,
                    TargetFormat.Exe
                )
                packageName = Config.namespace
                packageVersion = Config.majorVersionName
                description = Config.appDescription
                vendor = Config.vendorName
                licenseFile = rootProject.rootDir.resolve(Config.licenseFile)
                includeAllModules = true
                val iconDir = rootProject.rootDir.resolve("playstore")

                macOS {
                    packageName = Config.appName
                    dockName = Config.appName
                    setDockNameSameAsPackageName = false
                    bundleID = Config.namespace
                    appCategory = "public.app-category.developer-tools"
//                    iconFile = iconDir.resolve("icon_macos.icns")
                }
                windows {
                    dirChooser = true
                    menu = false
                    shortcut = true
                    perUserInstall = true
                    upgradeUuid = Config.appId
                    iconFile = iconDir.resolve("favicon.ico")
                }
                linux {
                    debMaintainer = Config.supportEmail
                    appCategory = "Development"
                    iconFile = iconDir.resolve("icon-512.png")
                }
            }
        }
    }
}

tasks.withType<JavaExec>().named { it == "composeApp:desktopRun" }
    .configureEach { mainClass = Config.mainClass }

junitPlatform {
    instrumentationTests.enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStandardStreams = true
    }
}

// Disable wasm tests for now
tasks.named("wasmJsBrowserTest") {
    enabled = false
}

tasks.register<Copy>("copyiOSTestResources") {
    from("src/commonTest/resources")
    into("build/bin/iosX64/debugTest/resources")
}

listOf("iosX64Test", "iosSimulatorArm64Test").forEach { name ->
    tasks.named(name).configure {
        copy {
            from("src/commonTest/resources")
            into("build/bin/${name.removeSuffix("Test")}/debugTest/resources")
        }
    }
}

kover {
    // Add in tests when ui is finalized
    disable()
    reports {
        verify {
            rule {
                bound { minValue = 21 }
            }
        }
        filters {
            excludes {
                androidGeneratedClasses()
                packages("timerx.fergdev.hagah.generated.resources")
                classes("*\$special$\$inlined\$map*")
                classes("*$\$inlined\$singleOf*")
                classes("*$\$inlined\$factoryOf\$default$*")
                classes("*$\$inlined\$activate\$default$*")
                classes("*$\$inlined\$dismiss\$default$*")
            }
        }
    }
}
