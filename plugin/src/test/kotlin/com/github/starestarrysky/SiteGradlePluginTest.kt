/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.github.starestarrysky

import com.github.starestarrysky.tasks.SiteTask
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test

/**
 * A simple unit test for the 'com.github.starestarrysky.site-gradle-plugin' plugin.
 */
class SiteGradlePluginTest {
    @Test fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.github.starestarrysky.site-gradle-plugin")
        project.tasks.register("site", SiteTask::class.java)

        // Verify the result
        val task = project.tasks.findByPath(":site")
        assert(task != null && task.actions.size > 0)
    }
}
