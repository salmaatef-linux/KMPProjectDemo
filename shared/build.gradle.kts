import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }
    
    js {
        browser()
        binaries.executable()
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    android {
       namespace = "com.example.kmpprojectdemo.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        val generatedSqlDelightSources = layout.buildDirectory
            .dir("generated/sqldelight/code/AppDatabase/commonMain")
            .get()
            .asFile
        val sqlDelightMain by creating {
            dependsOn(commonMain.get())
            // SQLDelight generates a common Kotlin API. Compile that API only in
            // the Android/iOS branch so it is never part of the wasmJs graph.
            kotlin.srcDir(generatedSqlDelightSources)
            kotlin.srcDir("src/nonWasmMain/kotlin")
            dependencies {
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)
            }
        }
        val commonMain = commonMain.get()
        // The SQLDelight plugin also registers this directory with commonMain.
        // Remove that source root so IDE analysis does not see the generated API
        // without the SQLDelight runtime on Wasm.
        commonMain.kotlin.srcDirs.remove(generatedSqlDelightSources)
        commonMain.kotlin.exclude("com/example/kmpprojectdemo/db/**")
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        androidMain.get().apply {
            dependsOn(sqlDelightMain)
            dependencies {
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.compose.uiTooling)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.security.crypto)
                implementation(libs.sqldelight.android)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
        }
        val iosMain = iosMain.get()
        iosArm64Main.get().dependsOn(iosMain)
        iosSimulatorArm64Main.get().dependsOn(iosMain)
        iosMain.apply {
            dependsOn(sqlDelightMain)
            dependencies {
                implementation(libs.ktor.client.darwin)
                implementation(libs.sqldelight.native)
            }
        }
        jsMain.get().apply {
            dependencies {
                implementation(libs.wrappers.browser)
                implementation(libs.ktor.client.js)
            }
        }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.example.kmpprojectdemo.db")
            srcDirs("src/nonWasmMain/sqldelight")
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

// SQLDelight's Gradle plugin contributes its generated common API to the npm
// aggregate. wasmJs does not use that API (it persists notes in localStorage),
// and SQLDelight 2.0.2 has no Wasm variant to resolve.
configurations.configureEach {
    if (name.startsWith("wasmJs")) {
        exclude(group = "app.cash.sqldelight")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-dom-api-compat")
    }
}
