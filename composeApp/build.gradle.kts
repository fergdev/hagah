import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mikepenz.aboutlibraries.plugin.StrictMode
import org.intellij.lang.annotations.Language
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id(libs.plugins.kotlinMultiplatform.id)
    id(libs.plugins.androidApplication.id)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.aboutLibs)
    alias(libs.plugins.kover)
}

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
    jvm("desktop")

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
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(Config.jvmTarget)
            freeCompilerArgs.addAll(Config.jvmCompilerArgs)
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateBuildConfig.map { it.destinationDir })

            @Suppress("OPT_IN_USAGE")
            compilerOptions {
                //                languageVersion.set(KotlinVersion.KOTLIN_2_0)
                freeCompilerArgs.addAll(Config.compilerArgs)
            }
            all {
                languageSettings {
                    //noinspection WrongGradleMethod
                    Config.optIns.forEach { optIn(it) }
                }

                dependencies {
                    implementation(projects.fcommon)
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
                }
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.compose.ui.tooling.preview)
                implementation(libs.androidx.foundation.layout.android)
                implementation(libs.koin.android)
                implementation(libs.kstore.file)
                implementation(libs.ktor.client.okhttp)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.kstore.file)
            }
        }
        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.apache)
            implementation(libs.appdirs)
            implementation(libs.kstore.file)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.kstore.storage)
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
                storeFile = File(rootDir, Config.storeFilePath)
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
                // Includes the default ProGuard rules files that are packaged with
                // the Android Gradle plugin. To learn more, go to the section about
                // R8 configuration files.
                getDefaultProguardFile("proguard-android-optimize.txt"),

                // Includes a local, custom Proguard rules file
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
    android { }
    web { }
    desktop {
        application {
            buildTypes.release.proguard {
                obfuscate = false
                optimize = false // TODO: Solve the issues with ktor and compose-desktop...
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

aboutLibraries {
    // - if the automatic registered android tasks are disabled, a similar thing can be achieved manually
    // - `./gradlew app:exportLibraryDefinitions -PaboutLibraries.exportPath=src/main/res/raw`
    // - the resulting file can for example be added as part of the SCM
    // registerAndroidTasks = false

    // Define the path configuration files are located in.
    // E.g. additional libraries, licenses to add to the target .json
    configPath = "config"

    // Allow to enable "offline mode", will disable any network check of the plugin
    // (including [fetchRemoteLicense] or pulling spdx license texts)
    offlineMode = false
    // enable fetching of "remote" licenses. Uses the GitHub API
    fetchRemoteLicense = true

    // Full license text for license IDs mentioned here will be included, even if no detected dependency uses them.
    // additionalLicenses = ["mit", "mpl_2_0"]

    // Allows to exclude some fields from the generated meta data field.
    // excludeFields = ["developers", "funding"]

    // Define the strict mode, will fail if the project uses licenses not allowed
    // - This will only automatically fail for Android projects which have `registerAndroidTasks` enabled
    // For non Android projects, execute `exportLibraryDefinitions`
    strictMode = StrictMode.FAIL
    // Allowed set of licenses, this project will be able to use without build failure
    allowedLicenses = arrayOf("Apache-2.0", "MIT", "BSD-3-Clause", "ASDKL", "NOASSERTION")
    // Allowed set of licenses for specific dependencies, this project will be able to use without build failure
    allowedLicensesMap = mapOf(Pair("asdkl", listOf("androidx.jetpack.library")))
    // Enable the duplication mode, allows to merge, or link dependencies which relate
    duplicationMode = DuplicateMode.MERGE
    // Configure the duplication rule, to match "duplicates" with
    duplicationRule = DuplicateRule.SIMPLE
    // Enable pretty printing for the generated JSON file
    prettyPrint = true
}

kover {
    reports {
        verify {
            rule {
                bound { minValue = 21 }
            }
        }
        filters {
            excludes {
                androidGeneratedClasses()
                annotatedBy("com.timerx.util.KoverIgnore")
                packages("timerx.shared.generated.resources")
                classes("*\$special$\$inlined\$map*")
                classes("*$\$inlined\$singleOf*")
                classes("*$\$inlined\$factoryOf\$default$*")
                classes("*$\$inlined\$activate\$default$*")
                classes("*$\$inlined\$dismiss\$default$*")
            }
        }
    }
}
