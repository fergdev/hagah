@file:Suppress(
    "MissingPackageDeclaration",
    "MemberVisibilityCanBePrivate",
)

import org.gradle.api.JavaVersion

object Config {
//        val jvmTarget = JvmTarget.JVM_11
//    val idePluginJvmTarget = JvmTarget.JVM_17

    const val appName = "Hagah"
    const val group = "com.fergdev"
    const val artifact = "hagah" // TODO Hagah
    const val artifactId = "$group.$artifact"

    // Versions
    const val versionCode = 1
    const val majorRelease = 1
    const val minorRelease = 0
    const val patch = 0
    const val majorVersionName = "$majorRelease.$minorRelease.$patch"
    const val versionName = "$majorRelease.$minorRelease.$patch"

    // Android
    const val namespace = artifactId

    // Desktop
    const val mainClass = "$namespace.MainKt"

    // Compose
    const val packageOfResClass = "$artifact.generated.resources"

    object KeyStore {
        const val propertiesFile = "keystore.properties"
        const val storePasswordKey = "storePassword"
        const val storeFileKey = "storeFile"
        const val keyPasswordKey = "keyPassword"
        const val aliasKey = "keyAlias"
    }

    @Suppress("MaxLineLength")
    const val appDescription =
        "TODO"
    const val vendorName = "Ferg.Dev"
    const val licenseFile = "LICENSE.txt"
    val javaVersion = JavaVersion.VERSION_17
    const val compileSdk = 35
    const val targetSdk = compileSdk
    const val minSdk = 21
    const val appId = "4a07189f-4143-4a90-9f53-1eedd74ddbeb"
    const val supportEmail = "ferg.dev@outlook.com"
    const val privacyPolicyUrl =
        "TODO"

    val stabilityLevels = listOf("snapshot", "eap", "preview", "alpha", "beta", "m", "cr", "rc")
    val minStabilityLevel = stabilityLevels.indexOf("beta")

    val optIns = listOf(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.coroutines.FlowPreview",
        "kotlin.RequiresOptIn",
        "kotlin.experimental.ExperimentalTypeInference",
        "kotlin.contracts.ExperimentalContracts",
        "org.jetbrains.compose.resources.ExperimentalResourceApi"
    )
    val compilerArgs = listOf(
        "-Xexpect-actual-classes",
        "-Xcontext-receivers",
        "-Xconsistent-data-class-copy-visibility"
    )
    val jvmCompilerArgs = buildList {
        addAll(compilerArgs)
        add("-Xjvm-default=all") // enable all jvm optimizations
        add("-Xcontext-receivers")
        add("-Xstring-concat=inline")
        add("-Xlambdas=indy")
    }

    object Detekt {
        const val CONFIG_FILE = "detekt.yml"
        val includedFiles = listOf("**/*.kt", "**/*.kts")
        val excludedFiles = listOf("**/resources/**", "**/build/**", "**/.idea/**")
    }
}
