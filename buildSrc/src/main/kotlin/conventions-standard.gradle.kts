plugins {
    java
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

group = "kr.toxicity.healthbar"
version = property("project_version").toString()

val shade = configurations.create("shade")

configurations.implementation {
    extendsFrom(shade)
}

rootProject.dependencies.dokka(project)

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
}

java {
    disableAutoTargetJvm()
    toolchain.languageVersion = JavaLanguageVersion.of(JAVA_VERSION)
}

kotlin {
    jvmToolchain(JAVA_VERSION)
}

dokka {
    moduleName = project.name
    dokkaSourceSets.configureEach {
        displayName = project.name
    }
}