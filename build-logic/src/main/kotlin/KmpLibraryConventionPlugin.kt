import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("com.android.library")
        }

        extensions.configure<LibraryExtension> {
            compileSdk = libs.findVersion("compileSdk").get().toString().toInt()
            defaultConfig.minSdk = libs.findVersion("minSdk").get().toString().toInt()
        }

        extensions.configure<KotlinMultiplatformExtension> {
            applyDefaultHierarchyTemplate()
            androidTarget()
            iosX64()
            iosArm64()
            iosSimulatorArm64()
            jvm("desktop")

            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
                freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
                freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
            }
        }
    }
}
