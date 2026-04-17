plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.plugins.android.application.toDep())
    compileOnly(libs.plugins.android.library.toDep())
    compileOnly(libs.plugins.kotlin.multiplatform.toDep())
    compileOnly(libs.plugins.compose.multiplatform.toDep())
    compileOnly(libs.plugins.compose.compiler.toDep())
}

fun Provider<PluginDependency>.toDep() = map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "convention.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("cmpFeature") {
            id = "convention.cmp.feature"
            implementationClass = "CmpFeatureConventionPlugin"
        }
        register("cmpApplication") {
            id = "convention.cmp.application"
            implementationClass = "CmpApplicationConventionPlugin"
        }
    }
}
