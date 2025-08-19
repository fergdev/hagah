@file:Suppress(
    "MissingPackageDeclaration",
    "MemberVisibilityCanBePrivate",
)

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Config {

    const val appName = "Hagah"
    const val group = "com.fergdev"
    const val artifact = "hagah"
    const val artifactId = "$group.$artifact"

    // Versions
    const val versionCode = 2
    const val majorRelease = 1
    const val minorRelease = 0
    const val patch = 1
    const val postfix = "-RC01" // include dash (-)
    const val majorVersionName = "$majorRelease.$minorRelease.$patch"
    const val versionName = "$majorVersionName$postfix"

    // Android
    const val namespace = artifactId
    const val compileSdk = 36
    const val targetSdk = compileSdk
    const val minSdk = 21

    // Desktop
    const val mainClass = "$namespace.MainKt"

    // Compose
    const val packageOfResClass = "$artifact.generated.resources"

    const val appDescription = "TODO"
    const val vendorName = "Ferg.Dev"
    const val licenseFile = "LICENSE.txt"
    const val appId = "4a07189f-4143-4a90-9f53-1eedd74ddbeb"
    const val supportEmail = "ferg.dev@outlook.com"
    const val privacyPolicyUrl = "https://doc-hosting.flycricket.io/hagah-privacy-policy/fa0b0892-fe29-44d4-a6ee-a879f9a2a0b3/privacy"

    // Versions
    val stabilityLevels = listOf("dev", "snapshot", "eap", "preview", "alpha", "beta", "m", "cr", "rc")
    val minStabilityLevel = stabilityLevels.indexOf("m")

    // Args
    val optIns = listOf(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.coroutines.FlowPreview",
        "kotlin.RequiresOptIn",
        "kotlin.experimental.ExperimentalTypeInference",
        "kotlin.contracts.ExperimentalContracts",
    )
    val appOptIns = buildList {
        addAll(optIns)
        add("org.jetbrains.compose.resources.ExperimentalResourceApi")
        add("com.russhwolf.settings.ExperimentalSettingsApi")
    }
    val compilerArgs = listOf(
        "-Xexpect-actual-classes",
        "-Xcontext-receivers",
        "-Xconsistent-data-class-copy-visibility",
    )
    val jvmCompilerArgs = buildList {
        addAll(compilerArgs)
        add("-Xjvm-default=all") // enable all jvm optimizations
        add("-Xcontext-receivers")
        add("-Xstring-concat=inline")
        add("-Xlambdas=indy")
    }

    // JVM
    val jvmTarget = JvmTarget.JVM_24
    val idePluginJvmTarget = JvmTarget.JVM_24
    val javaVersion = JavaVersion.VERSION_24
    val javaMajorVersion = javaVersion.ordinal + 1

    object Detekt {
        const val CONFIG_FILE = "detekt.yml"
        val includedFiles = listOf("**/*.kt", "**/*.kts")
        val excludedFiles = listOf("**/resources/**", "**/build/**", "**/.idea/**")
    }
}
