import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.conventions.paperweight)
}

dependencies {
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.release = 21
    }
    compileKotlin {
        compilerOptions.jvmTarget = JvmTarget.JVM_21
        // Paper's CraftLivingEntity.getEquipment() is @NotNull, but returns null at runtime for
        // entities without equipment (and during unload). Suppress Kotlin's not-null call assertion
        // so the genuine null passes through to callers (e.g. MythicMobs) that already null-check it.
        compilerOptions.freeCompilerArgs.add("-Xno-call-assertions")
    }
}