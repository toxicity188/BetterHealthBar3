pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}


plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "BetterHealthBar"

include(
    "api",
    "dist",

    "scheduler:standard",
    "scheduler:folia",

    "bedrock:geyser",
    "bedrock:floodgate",

    "modelengine:legacy",
    "modelengine:current",

    "nms:v1_19_R3",
    "nms:v1_20_R1",
    "nms:v1_20_R2",
    "nms:v1_20_R3",
    "nms:v1_20_R4",
    "nms:v1_21_R1",
    "nms:v1_21_R2",
    "nms:v1_21_R3",
    "nms:v1_21_R4",
    "nms:v1_21_R5",
)