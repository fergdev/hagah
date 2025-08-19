import nl.littlerobots.vcu.plugin.versionSelector
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradleSubplugin
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    alias(libs.plugins.gradleDoctor)
    alias(libs.plugins.version.catalog.update)

    // TODO: https://github.com/kotest/kotest/issues/3598
    // alias(libs.plugins.kotest) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false

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
            featureFlags.addAll(ComposeFeatureFlag.OptimizeNonSkippingGroups)
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
    keep {
        keepUnusedVersions = true
    }
}
doctor {
    disallowMultipleDaemons.set(false)
    GCWarningThreshold.set(0.10f)
    GCFailThreshold = 0.9f
    warnWhenJetifierEnabled.set(true)
    negativeAvoidanceThreshold.set(500)
    disallowCleanTaskDependencies.set(true)
    warnIfKotlinCompileDaemonFallback.set(true)
    javaHome {
        ensureJavaHomeMatches.set(true)
        ensureJavaHomeIsSet.set(true)
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
