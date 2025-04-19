plugins {
    id(libs.plugins.kotlinMultiplatform.id)
    alias(libs.plugins.kotlinxSerialization)
}
kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    @Suppress("OPT_IN_USAGE")
    wasmJs {
        browser()
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
    jvm().compilations.all {
        compileTaskProvider.configure {
            compilerOptions {
                jvmTarget = Config.jvmTarget
                freeCompilerArgs.addAll(Config.jvmCompilerArgs)
                progressiveMode.set(true)
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.datetime)
        }
    }
}
