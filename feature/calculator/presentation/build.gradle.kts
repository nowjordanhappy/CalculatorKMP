import org.gradle.api.artifacts.VersionCatalogsExtension

plugins { alias(libs.plugins.convention.cmp.feature) }

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
val lifecycleViewModel = catalog.findLibrary("lifecycle-viewmodel").get()
val lifecycleRuntime = catalog.findLibrary("lifecycle-runtime").get()
val koinCore = catalog.findLibrary("koin-core").get()
val koinCompose = catalog.findLibrary("koin-compose").get()
val koinViewModel = catalog.findLibrary("koin-composevm").get()

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
            implementation(lifecycleViewModel)
            implementation(lifecycleRuntime)
            implementation(koinCore)
            implementation(koinCompose)
            implementation(koinViewModel)
        }
        commonTest.dependencies { implementation(kotlin("test")) }
    }
}

android { namespace = "com.nowjordanhappy.calculatorkmp.feature.calculator.presentation" }
