plugins {
    alias(libs.plugins.conventions.paperweight)
}

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")
}

tasks {
    compileKotlin {
        // Paper's CraftLivingEntity.getEquipment() is @NotNull, but returns null at runtime for
        // entities without equipment (and during unload). Suppress Kotlin's not-null call assertion
        // so the genuine null passes through to callers (e.g. MythicMobs) that already null-check it.
        compilerOptions.freeCompilerArgs.add("-Xno-call-assertions")
    }
}