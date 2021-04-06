/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Gradle plugin project to get you started.
 * For more details take a look at the Writing Custom Plugins chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.8.3/userguide/custom_plugins.html
 */

plugins {
    `java-gradle-plugin`
    kotlin("jvm")
}

base {
    archivesBaseName = "plugin"
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation("org.kohsuke:github-api:1.123")
}

gradlePlugin {
    val siteGradlePlugin by plugins.creating {
        id = "com.github.starestarrysky.site-gradle-plugin"
        implementationClass = "com.github.starestarrysky.SiteGradlePlugin"
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

tasks.check {
    dependsOn(functionalTest)
}


val generateSourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().java.srcDirs)
}
