plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.mokkery)
}
kotlin {

    // Target declarations
    androidLibrary {
        namespace = "thisissadeghi.sample"
    }
    jvm("desktop")

    val xcfName = "sample"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.uiUtil)
                implementation(compose.materialIconsExtended)
                implementation(compose.material3)
                implementation(compose.components.resources)

                implementation(libs.kotlinCollection)
                implementation(libs.kotlinxSerialization)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                api(libs.koin.core)
                implementation(libs.jetbrains.compose.navigation)

                implementation(project(":core:designsystem"))
                implementation(project(":core:common"))
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.testing.common)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
                implementation(libs.turbine)
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}
