plugins {
    id("buildlogic-conventions")
    `groovy-gradle-plugin`
}

dependencies {
    implementation("me.champeau.gradle:japicmp-gradle-plugin")

    implementation("gradlebuild:basics")
    implementation("gradlebuild:module-identity")

    implementation("com.google.code.gson:gson")
    implementation("com.google.guava:guava")
    implementation("org.javassist:javassist")
    implementation("com.github.javaparser:javaparser-core")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm")
    implementation(kotlin("compiler-embeddable"))

    testImplementation("org.jsoup:jsoup")
}

tasks.compileGroovy.configure {
    classpath += files(tasks.compileKotlin)
}