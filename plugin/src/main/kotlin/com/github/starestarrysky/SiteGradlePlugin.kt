/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.github.starestarrysky

import com.github.starestarrysky.extension.SiteGradlePluginExtension
import org.gradle.api.Project
import org.gradle.api.Plugin

/**
 * A simple 'hello world' plugin.
 */
class SiteGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("site", SiteGradlePluginExtension::class.java)
        // Register a task
        project.tasks.register("siteGradlePlugin") { task ->
            task.doLast {
                println("Hello ${extension.people} from plugin '${extension.message}'")
            }
        }
    }
}
