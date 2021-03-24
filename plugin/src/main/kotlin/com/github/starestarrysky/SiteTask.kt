package com.github.starestarrysky

import com.github.starestarrysky.extension.GitHubCredentials
import com.github.starestarrysky.tasks.GitHubCredentialsAware
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

@CompileStatic
class SiteTask : DefaultTask(), GitHubCredentialsAware {
    companion object {
        const val BRANCH_DEFAULT = "refs/heads/main"
        const val NO_JEKYLL_FILE = ".nojekyll"
    }

    @get:Input
    val repositoryName = project.objects.setProperty(String::class.java).empty()

    @get:Input
    val repositoryOwner = project.objects.setProperty(String::class.java).empty()

    @get:Input
    val branch = project.objects.property(String::class.java).set(BRANCH_DEFAULT)

    @get:Input
    @Optional
    val merge = project.objects.property(Boolean::class.java).set(true)

    @get:Input
    @Optional
    val noJekyll = project.objects.property(Boolean::class.java).set(true)

    @get:Input
    val message = project.objects.setProperty(String::class.java).empty()

    @get:Input
    @Optional
    val includes = project.objects.listProperty(String::class.java).set(arrayListOf("**/*"))

    @get:Input
    @Optional
    val excludes = project.objects.listProperty(String::class.java).empty()

    @get:InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    @Optional
    val outputDirectory = project.objects.fileProperty().set(project.layout.buildDirectory.file("")) // TODO: 2021/3/24 deploy files

    override var gitHubCredentials = project.objects.newInstance(GitHubCredentials::class.java)

    @TaskAction
    fun execute() {

    }

    override fun credentials(action: Action<in GitHubCredentials>) {
        action.execute(gitHubCredentials)
    }
}
