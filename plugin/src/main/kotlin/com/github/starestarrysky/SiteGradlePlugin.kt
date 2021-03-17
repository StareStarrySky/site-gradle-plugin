/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.github.starestarrysky

import org.gradle.api.Project
import org.gradle.api.Plugin

/**
 * A simple 'hello world' plugin.
 */
class SiteGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        // Register a task
        project.tasks.register("siteGradlePlugin") { task ->
            task.doLast {
                println("Hello from plugin 'com.github.starestarrysky'")
            }
        }
    }
}
