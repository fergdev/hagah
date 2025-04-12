import nl.littlerobots.vcu.plugin.versionSelector
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradleSubplugin

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    alias(libs.plugins.gradleDoctor)
    alias(libs.plugins.version.catalog.update)

    alias(libs.plugins.kotest) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.aboutLibs) apply false

//    Applied by convention
//    alias(libs.plugins.androidApplication) apply false
//    alias(libs.plugins.kotlinMultiplatform) apply false
//    alias(libs.plugins.kotlinJvm) apply false
//    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
}

allprojects {
    group = Config.artifactId
    version = Config.versionName
}

subprojects {
    plugins.withType<ComposeCompilerGradleSubplugin>().configureEach {
        the<ComposeCompilerGradlePluginExtension>().apply {
            featureFlags.addAll(
//                ComposeFeatureFlag.OptimizeNonSkippingGroups,
//                ComposeFeatureFlag.StrongSkipping
            )
            stabilityConfigurationFiles =
                listOf(rootProject.layout.projectDirectory.file("stability_definitions.txt"))
            if (properties["enableComposeCompilerReports"] == "true") {
                val metricsDir = layout.buildDirectory.dir("compose_metrics")
                metricsDestination = metricsDir
                reportsDestination = metricsDir
            }
        }
    }
    //noinspection WrongGradleMethod
    tasks {
        withType<Test>().configureEach {
            useJUnitPlatform()
            filter { isFailOnNoMatchingTests = true }
        }
    }
}

versionCatalogUpdate {
    sortByKey = true
    versionSelector { stabilityLevel(it.candidate.version) >= Config.minStabilityLevel }
//    keep {
//        keepUnusedVersions = true
//        keepUnusedLibraries = true
//        keepUnusedPlugins = true
//    }
}
doctor {
    disallowMultipleDaemons.set(false)
    downloadSpeedWarningThreshold.set(.5f)
    GCWarningThreshold.set(0.10f)
    GCFailThreshold = 0.9f
    failOnEmptyDirectories.set(true)
    warnWhenJetifierEnabled.set(true)
    negativeAvoidanceThreshold.set(500)
    warnWhenNotUsingParallelGC.set(true)
    disallowCleanTaskDependencies.set(true)
    warnIfKotlinCompileDaemonFallback.set(true)
    javaHome {
        // TODO set these back to true
        ensureJavaHomeMatches.set(false)
        ensureJavaHomeIsSet.set(false)
        failOnError.set(true)
    }
}

dependencies {
    detektPlugins(rootProject.libs.detekt.formatting)
    detektPlugins(rootProject.libs.detekt.compose)
    detektPlugins(rootProject.libs.detekt.libraries)
}

tasks {
    withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        buildUponDefaultConfig = true
        parallel = true
        setSource(projectDir)
        config.setFrom(File(rootDir, Config.Detekt.CONFIG_FILE))
        basePath = projectDir.absolutePath
        include(Config.Detekt.includedFiles)
        exclude(Config.Detekt.excludedFiles)
        reports {
            xml.required.set(false)
            html.required.set(true)
            txt.required.set(false)
            sarif.required.set(true)
            md.required.set(false)
        }
    }

    register<io.gitlab.arturbosch.detekt.Detekt>("detektFormat") {
        description = "Formats whole project."
        autoCorrect = true
    }

    register<io.gitlab.arturbosch.detekt.Detekt>("detektAll") {
        description = "Run detekt on whole project"
        autoCorrect = false
    }
}
