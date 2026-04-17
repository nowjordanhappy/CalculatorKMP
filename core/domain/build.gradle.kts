import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    alias(libs.plugins.convention.kmp.library)
}

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
val koinCore = catalog.findLibrary("koin-core").get()

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(koinCore)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "com.jordanrojas.calculatorkmp.core.domain"
}
