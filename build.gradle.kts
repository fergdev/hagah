import nl.littlerobots.vcu.plugin.versionSelector

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotest)
    alias(libs.plugins.kover)
    alias(libs.plugins.gradleDoctor)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.aboutLibs) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
}

allprojects {
    group = Config.artifactId
    version = Config.versionName
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
