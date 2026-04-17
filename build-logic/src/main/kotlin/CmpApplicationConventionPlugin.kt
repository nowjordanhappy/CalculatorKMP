import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class CmpApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("com.android.application")
            apply("org.jetbrains.compose")
            apply("org.jetbrains.kotlin.plugin.compose")
        }

        extensions.configure<BaseAppModuleExtension> {
            compileSdk = libs.findVersion("compileSdk").get().toString().toInt()
            defaultConfig {
                minSdk = libs.findVersion("minSdk").get().toString().toInt()
                targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
            }
            packaging {
                resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
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
