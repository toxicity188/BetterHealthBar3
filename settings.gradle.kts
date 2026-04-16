pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.opencollab.dev/main/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.skriptlang.org/releases")
        maven("https://repo.alessiodp.com/releases/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://maven.citizensnpcs.co/repo/")
        maven("https://repo.alessiodp.com/releases/")
        maven("https://jitpack.io/")
        maven("https://mvn.lumine.io/repository/maven-public/")
    }
}

rootProject.name = "BetterHealthBar"

include(
    "api",
    "dist",

    "bedrock:geyser",
    "bedrock:floodgate",

    "modelengine:legacy",
    "modelengine:current",

    "nms:v1_20_R4",
    "nms:v1_21_R1",
    "nms:v1_21_R2",
    "nms:v1_21_R3",
    "nms:v1_21_R4",
    "nms:v1_21_R5",
    "nms:v1_21_R6",
    "nms:v1_21_R7",
    "nms:v26_R1",
)