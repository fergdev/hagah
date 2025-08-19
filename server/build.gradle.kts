plugins {
    alias(serverLibs.plugins.ktor)
    id(libs.plugins.kotlinJvm.get().pluginId)
    alias(libs.plugins.kotlinxSerialization)
    application
}

group = Config.group
version = Config.versionName

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("src/main/kotlin")
            compilerOptions {
                jvmTarget = Config.jvmTarget
                freeCompilerArgs.addAll(Config.jvmCompilerArgs)
                progressiveMode.set(true)
            }
        }
    }
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(Config.javaVersion.majorVersion))
    }
}

ktor {
    fatJar {
        archiveFileName.set("hagah-server.jar")
    }
}
application {
    mainClass.set("com.fergdev.hagah.server.ApplicationKt")
    applicationDefaultJvmArgs =
        listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {

    implementation(projects.data)
    implementation(serverLibs.logback)
    implementation(libs.kotlinx.datetime)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(serverLibs.ktor.server.core)
    implementation(serverLibs.ktor.server.netty)
    implementation(libs.arrow.core)
    implementation(libs.ktorm.core)
    implementation(libs.sqlite.jdbc)
    implementation(libs.kodein.di)
    implementation(libs.kodein.di.framework.ktor.server.jvm)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.network.tls.certificates)
    implementation(libs.ktor.server.netty)
    implementation(libs.dotenv.kotlin)

    testImplementation(serverLibs.kotlin.test.junit)
}

repositories {
    mavenCentral()
}
