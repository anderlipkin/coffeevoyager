package com.coffeevoyager

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named

internal fun Project.configureDetekt(extension: DetektExtension) = extension.apply {
    tasks.named<Detekt>("detekt") {
        // Version of detekt that will be used. When unspecified the latest detekt
        // version found will be used. Override to stay on the same version.
        toolVersion = libs.findVersion("detekt").get().requiredVersion

        // Builds the AST in parallel. Rules are always executed in parallel.
        // Can lead to speedups in larger projects. `false` by default.
        parallel = true

        // Define the detekt configuration(s) you want to use.
        // Defaults to the default detekt configuration.
        config = files("$rootDir/detekt.yml")

        // Applies the config files on top of detekt's default config file. `false` by default.
        buildUponDefaultConfig = true

        // Turns on all the rules. `false` by default.
        allRules = false

        // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
//        baseline = file("path/to/baseline.xml")

        // Disables all default detekt rulesets and will only run detekt with custom rules
        // defined in plugins passed in with `detektPlugins` configuration. `false` by default.
        disableDefaultRuleSets = false

        // Adds debug output during task execution. `false` by default.
        debug = false

        // If set to `true` the build does not fail when the
        // maxIssues count was reached. Defaults to `false`.
        ignoreFailures = false

        // Android: Don't create tasks for the specified build types (e.g. "release")
        ignoredBuildTypes = listOf("release")

        // Android: Don't create tasks for the specified build flavor (e.g. "production")
//        ignoredFlavors = listOf("prod")

        // Android: Don't create tasks for the specified build variants (e.g. "productionRelease")
//        ignoredVariants = listOf("prodRelease")

        // Specify the base path for file paths in the formatted reports.
        // If not set, all file paths reported will be absolute file path.
        basePath = projectDir.absolutePath
    }

    dependencies {
        add("detektPlugins", libs.findLibrary("detekt-formatting").get())
        add("detektPlugins", libs.findLibrary("detekt-compose").get())
    }
}