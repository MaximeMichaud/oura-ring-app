import dev.detekt.gradle.extensions.DetektExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt) apply false
}

// Spotless (formatting via ktlint)
spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
            .setEditorConfigPath("$rootDir/.editorconfig")
            .editorConfigOverride(
                mapOf(
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                ),
            )
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
    }
}

// Detekt applied to subprojects with Kotlin sources
subprojects {
    apply(plugin = "dev.detekt")

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom("$rootDir/config/detekt/detekt.yml")
        parallel = true
    }

    dependencies {
        "detektPlugins"(rootProject.libs.detekt.compose.rules)
    }
}

// Auto-install git hooks on project sync
tasks.register<Copy>("installGitHooks") {
    from("$rootDir/scripts/pre-commit")
    into("$rootDir/.git/hooks")
    filePermissions {
        user {
            read = true
            write = true
            execute = true
        }
    }
}
