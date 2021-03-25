package com.github.starestarrysky.extension

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

@CompileStatic
class GitHubCredentials {
    @get:Input
    @get:Optional
    var userName: String? = null

    @get:Input
    @get:Optional
    var password: String? = null

    @get:Input
    @get:Optional
    var oauthToken: String? = null
}
