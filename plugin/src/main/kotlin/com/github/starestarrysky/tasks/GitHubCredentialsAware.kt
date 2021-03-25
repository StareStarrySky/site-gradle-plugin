package com.github.starestarrysky.tasks

import com.github.starestarrysky.extension.GitHubCredentials
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.tasks.Nested

@CompileStatic
interface GitHubCredentialsAware : Task {
    var gitHubCredentials: GitHubCredentials
        @get:Nested
        get

    fun credentials(action: Action<in GitHubCredentials>)
}
