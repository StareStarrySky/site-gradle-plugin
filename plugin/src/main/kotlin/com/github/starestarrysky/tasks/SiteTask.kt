package com.github.starestarrysky.tasks

import com.github.starestarrysky.extension.GitHubCredentials
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.tasks.Optional
import org.kohsuke.github.GHCommit
import org.kohsuke.github.GHTree
import org.kohsuke.github.GitHubBuilder
import java.util.Properties
import java.util.GregorianCalendar

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
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val outputDirectory = project.objects.fileProperty()

    @get:Input
    @get:Optional
    val skip = project.objects.property(Boolean::class.java)

    @get:Input
    @get:Optional
    val dryRun = project.objects.property(Boolean::class.java)

    @Internal
    override var credentials = project.objects.newInstance(GitHubCredentials::class.java)

    init {
        repositoryName.set(null)
        repositoryOwner.set(null)
        branch.set(BRANCH_DEFAULT)
        noJekyll.set(true)
        message.set(null)
        includes.empty()
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
            // check config param
            val checkConfig = arrayListOf(repositoryName.get(), repositoryOwner.get(), message.get())
            checkConfig.iterator().forEach { config ->
                if (StringUtils.isBlank(config)) {
                    logger.error("{0} can't be blank.", config)
                }
            }

            if (dryRun.get()) {
                logger.info("Dry run mode, repository will not be modified.")
            }

            // get github and repository
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
                logger.debug("Scanning {0} and including {1} and excluding {2}.",
                    baseDir, includes.get().joinToString(prefix = "[", postfix = "]"),
                    excludes.get().joinToString(prefix = "[", postfix = "]"))
            }

            // match files
            val includeFiles = project.fileTree(baseDir).matching { match -> match.include(includes.get()) }
            val fileTree = if (excludes.get().size == 0) {
                includeFiles
            } else {
                val excludeFiles = project.fileTree(baseDir).matching { match -> match.include(excludes.get()) }
                includeFiles.minus(excludeFiles)
            }

            if (logger.isDebugEnabled) {
                logger.debug("Scanned files to include: {0}.", fileTree.asPath)
            }
            logger.info("Creating {0} blobs.", fileTree.count())

            // .nojekyll
            var createNoJekyll = noJekyll.get()
            val ghTreeBuilder = repository.createTree()
            var treeNum = 0
            fileTree.files.forEach { file ->
                ghTreeBuilder.add(file.relativeTo(outputDirectory.get().asFile).invariantSeparatorsPath, file.readBytes(), false)
                treeNum ++
                if (createNoJekyll && NO_JEKYLL_FILE == file.name) {
                    createNoJekyll = false
                }
            }

            if (createNoJekyll) {
                if (logger.isDebugEnabled) {
                    logger.debug("Creating empty .nojekyll blob at root of tree.")
                }
                if (!dryRun.get()) {
                    ghTreeBuilder.add(NO_JEKYLL_FILE, ByteArray(0), false)
                    treeNum ++
                }
            }

            // get branch
            logger.info("Creating tree with {0} blob entries.", treeNum)
            val ghRef = repository.getRef(branch.get().substring(branch.get().indexOf("/") + 1))

            // create tree and commit
            logger.info("Merging with tree {0}.", ghRef.`object`.sha)
            val ghTree = if (!dryRun.get()) ghTreeBuilder.create() else GHTree()
            val time = GregorianCalendar().time
            val ghCommit = if (dryRun.get()) GHCommit() else repository.createCommit()
                .parent(ghRef.`object`.sha)
                .tree(ghTree.sha)
                .message(message.get())
                .author(gitHub.myself.name, gitHub.myself.email, time)
                .committer(gitHub.myself.name, gitHub.myself.email, time)
                .create()

            // merge branch
            logger.info("Creating commit with SHA-1: {0}.", ghCommit.shA1)
            if (!dryRun.get()) {
                ghRef.updateTo(ghCommit.shA1)
            }

            logger.info("Updating reference {0} from {1} to {2}.",
                BRANCH_DEFAULT, ghRef.`object`.sha, ghCommit.shA1)
        }
    }

    override fun credentials(action: Action<in GitHubCredentials>) {
        action.execute(credentials)
    }
}
