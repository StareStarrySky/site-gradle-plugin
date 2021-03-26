package com.github.starestarrysky

import com.github.starestarrysky.extension.GitHubCredentials
import com.github.starestarrysky.tasks.GitHubCredentialsAware
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.tasks.Optional
import org.kohsuke.github.GitHubBuilder
import java.util.*
import kotlin.system.exitProcess

@CompileStatic
open class SiteTask : DefaultTask(), GitHubCredentialsAware {
    companion object {
        const val BRANCH_DEFAULT = "refs/heads/main"
        const val NO_JEKYLL_FILE = ".nojekyll"
    }

    @get:Input
    val repositoryName = project.objects.property(String::class.java)

    @get:Input
    val repositoryOwner = project.objects.property(String::class.java)

    @get:Input
    val branch = project.objects.property(String::class.java)

    @get:Input
    @get:Optional
    val merge = project.objects.property(Boolean::class.java)

    @get:Input
    @get:Optional
    val noJekyll = project.objects.property(Boolean::class.java)

    @get:Input
    val message = project.objects.property(String::class.java)

    @get:Input
    @get:Optional
    val includes = project.objects.listProperty(String::class.java)

    @get:Input
    @get:Optional
    val excludes = project.objects.listProperty(String::class.java)

    @get:InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    val outputDirectory = project.objects.fileProperty()

    @get:Input
    @get:Optional
    val skip = project.objects.property(Boolean::class.java)

    @get:Input
    @get:Optional
    val dryRun = project.objects.property(Boolean::class.java)

    override var credentials = project.objects.newInstance(GitHubCredentials::class.java)

    init {
        repositoryName.set(null)
        repositoryOwner.set(null)
        branch.set(BRANCH_DEFAULT)
        merge.set(true)
        noJekyll.set(true)
        message.set(null)
        includes.set(arrayListOf("**/*"))
        excludes.empty()
        outputDirectory.set(project.layout.buildDirectory.file("~/.m2/repository"))
        skip.set(false)
        dryRun.set(false)
    }

    @TaskAction
    fun execute() {
        if (skip.get()) {
            logger.info("Github Site Plugin execution skipped.")
        } else {
            checkConfig(repositoryName.get(), repositoryOwner.get(), message.get())

            if (dryRun.get()) {
                logger.info("Dry run mode, repository will not be modified.")
            }

            val gitHub = GitHubBuilder.fromProperties(Properties().apply {
                if (credentials.userName != null) {
                    setProperty("login", credentials.userName)
                }
                if (credentials.password != null) {
                    setProperty("password", credentials.password)
                }
                if (credentials.oauthToken != null) {
                    setProperty("oauth", credentials.oauthToken)
                }
            }).build()
            val repository = gitHub.getRepository("${repositoryOwner.get()}/${repositoryName.get()}")

            val baseDir = outputDirectory.get().asFile.absolutePath

            if (logger.isDebugEnabled) {
                logger.debug("Scanning {0} and including {1} and excluding {2}",
                    baseDir,
                    includes.get().joinToString(prefix = "[", postfix = "]"),
                    excludes.get().joinToString(prefix = "[", postfix = "]"),
                )
            }

            val includeFiles = project.fileTree(baseDir).matching { match -> match.include(includes.get()) }
            val excludeFiles = project.fileTree(baseDir).matching { match -> match.include(excludes.get()) }
            val fileTree = includeFiles.minus(excludeFiles)

            // TODO: 2021/3/26 continue
        }
    }

    private fun checkConfig(vararg configs: String) {
        configs.iterator().forEach { config ->
            if (StringUtils.isBlank(config)) {
                logger.error("{} can't be blank.", config)
            }
        }
    }

    override fun credentials(action: Action<in GitHubCredentials>) {
        action.execute(credentials)
    }
}
