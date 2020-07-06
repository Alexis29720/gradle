/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

pluginManagement {
    repositories {
        maven {
            name = "kotlin-eap"
            url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
        }
        gradlePluginPortal()
    }
}

apply(from = "../gradle/shared-with-buildSrc/mirrors.settings.gradle.kts")

val upperCaseLetters = "\\p{Upper}".toRegex()

fun String.toKebabCase() =
    replace(upperCaseLetters) { "-${it.value.toLowerCase()}" }

rootProject.name = "buildSrc"

// Platform: defines shared dependency versions
include("buildPlatform")

// Utilities for updating the build itself which are not part of the usual build process
include("buildUpdateUtils")

// Shared basics for all
include("basics")

// Compute the identity/version we are building and related details (like current git commit)
include("moduleIdentity")

// Shared information about external modules
include("dependencyModules")

// Special purpose build logic for root project - please preserve alphabetical order
include("cleanup")
include("idea")
include("lifecycle")

// Special purpose build logic for subproject - please preserve alphabetical order
include("binaryCompatibility")
include("buildquality")
include("documentation")
include("integrationTesting")
include("jvm")
include("kotlinDsl")
include("uberPlugins")
include("packaging")
include("performanceTesting")
include("profiling")
include("publishing")

fun buildFileNameFor(projectDirName: String) =
    "$projectDirName.gradle.kts"

for (project in rootProject.children) {
    val projectDirName = project.name.toKebabCase()
    project.projectDir = file("subprojects/$projectDirName")
    project.buildFileName = buildFileNameFor(projectDirName)
    assert(project.projectDir.isDirectory)
    assert(project.buildFile.isFile)
}

fun remoteBuildCacheEnabled(settings: Settings) = settings.buildCache.remote?.isEnabled == true

fun isAdoptOpenJDK() = true == System.getProperty("java.vendor")?.contains("AdoptOpenJDK")

fun isAdoptOpenJDK11() = isAdoptOpenJDK() && JavaVersion.current().isJava11

fun getBuildJavaHome() = System.getProperty("java.home")

gradle.settingsEvaluated {
    if ("true" == System.getProperty("org.gradle.ignoreBuildJavaVersionCheck")) {
        return@settingsEvaluated
    }

    if (remoteBuildCacheEnabled(this) && !isAdoptOpenJDK11()) {
        throw GradleException("Remote cache is enabled, which requires AdoptOpenJDK 11 to perform this build. It's currently ${getBuildJavaHome()}.")
    }
}