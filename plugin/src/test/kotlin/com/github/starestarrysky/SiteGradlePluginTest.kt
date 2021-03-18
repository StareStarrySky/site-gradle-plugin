/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.github.starestarrysky

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * A simple unit test for the 'com.github.starestarrysky.site-gradle-plugin' plugin.
 */
class SiteGradlePluginTest {
    @Test fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.github.starestarrysky.site-gradle-plugin")

        // Verify the result
        assertNotNull(project.tasks.findByName("siteGradlePlugin"))
    }
}
