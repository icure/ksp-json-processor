plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.icure"
version = "1.0.0"

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(libs.ksp.symbol.processing.api)
                implementation(libs.kotlin.serialization)
                implementation(libs.kotlin.reflection)
                implementation(libs.icure.charix)
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}
