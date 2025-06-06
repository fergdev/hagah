plugins {
    alias(serverLibs.plugins.ktor)
    id(libs.plugins.kotlinJvm.id)
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
        languageVersion.set(JavaLanguageVersion.of(17))
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
    implementation("org.ktorm:ktorm-core:3.6.0")
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    implementation("org.kodein.di:kodein-di:7.20.2")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.20.2")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
    implementation("io.ktor:ktor-client-cio:3.1.2")
    implementation("io.ktor:ktor-network-tls-certificates:3.1.2")
    implementation("io.ktor:ktor-server-netty:3.1.2")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    testImplementation(serverLibs.kotlin.test.junit)
}

repositories {
    mavenCentral()
}
