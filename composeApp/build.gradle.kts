import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    android {
        namespace = "thisissadeghi.kmpilot"
    }
    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.util)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
            implementation(libs.kotlinxSerialization)
            implementation(libs.jetbrains.compose.navigation)
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(project(":core:data"))
            implementation(project(":core:common"))
            implementation(project(":core:designsystem"))
            implementation(project(":feature:sample"))
            implementation(project(":feature:send"))
            implementation(project(":feature:receive"))
        }
    }
}

base.archivesName.set("KMPilot-${libs.versions.android.versionName.get()}")

buildkonfig {
    packageName = "thisissadeghi.kmpilot"

    defaultConfigs {
        buildConfigField(
            INT,
            "VERSION_CODE",
            libs.versions.android.versionCode
                .get(),
        )
        buildConfigField(
            STRING,
            "VERSION_NAME",
            libs.versions.android.versionName
                .get(),
        )
    }

    // Develop flavor
    defaultConfigs("develop") {
        buildConfigField(STRING, "BASE_URL", "https://api.example.com/")
        buildConfigField(STRING, "FLAVOR_NAME", "develop")
    }

    // Production flavor
    defaultConfigs("production") {
        buildConfigField(STRING, "BASE_URL", "https://api.example.com/")
        buildConfigField(STRING, "FLAVOR_NAME", "production")
    }
}
