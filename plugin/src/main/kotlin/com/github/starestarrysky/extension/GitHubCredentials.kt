package com.github.starestarrysky.extension

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

@CompileStatic
class GitHubCredentials {
    companion object {
        const val DEFAULT_URL = "https://api.github.com"
    }

    @get:Input
    @Optional
    var url: String = DEFAULT_URL

    @get:Input
    @Optional
    var userName: String? = null

    @get:Input
    var oauthAccessToken: String? = null
}
