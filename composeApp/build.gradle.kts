import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinKsp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
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

        androidMain.dependencies {
            // android only
            implementation(libs.koin.android)
            implementation(libs.activityCompose)
            implementation(compose.uiTooling)
            implementation(compose.preview)
        }

        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.uiUtil)
            implementation(compose.materialIconsExtended)
            implementation(compose.material3)
            implementation(compose.components.resources)
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

android {
    namespace = "thisissadeghi.kmpilot"

    defaultConfig {
        applicationId = "com.thisissadeghi.kmpilot"
        versionCode =
            libs.versions.android.versionCode
                .get()
                .toInt()
        versionName =
            libs.versions.android.versionName
                .get()
        setProperty("archivesBaseName", "KMPilot-$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    val keystoreParent = File("${rootDir.path}/signing")
    val keystorePropFile = File(keystoreParent, "base-keystore.properties")
    if (keystorePropFile.exists()) {
        signingConfigs {
            val props = Properties().apply { load(FileInputStream(keystorePropFile)) }
            create("mainKey") {
                storeFile = File(keystoreParent, props["storeFile"].toString())
                storePassword = props["storePassword"].toString()
                keyAlias = props["keyAlias"].toString()
                keyPassword = props["keyPassword"].toString()
            }
        }
    } else {
        println("signing key not found!")
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfigs.findByName("mainKey")?.let { signingConfig = it }
        }
        debug {
            versionNameSuffix = "-SNAPSHOT"
            signingConfigs.findByName("mainKey")?.let { signingConfig = it }
        }
    }
}

buildkonfig {
    packageName = "thisissadeghi.kmpilot"

    val iosTargets = listOf("iosX64", "iosArm64", "iosSimulatorArm64")

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
