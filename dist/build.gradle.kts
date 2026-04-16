import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    alias(libs.plugins.conventions.paper)
    alias(libs.plugins.conventions.core)
    id("com.gradleup.shadow")
    id("xyz.jpenilla.resource-factory-bukkit-convention")
}

dependencies {
    shade(libs.bundles.library.shaded)

    shade(project(":bedrock:geyser")) { isTransitive = false }
    shade(project(":bedrock:floodgate")) { isTransitive = false }
    shade(project(":modelengine:legacy")) { isTransitive = false }
    shade(project(":modelengine:current")) { isTransitive = false }

    shade(project(":nms:v1_20_R4", configuration = "reobf")) { isTransitive = false }
    shade(project(":nms:v1_21_R1", configuration = "reobf")) { isTransitive = false }
    shade(project(":nms:v1_21_R2", configuration = "reobf")) { isTransitive = false }
    shade(project(":nms:v1_21_R3", configuration = "reobf")) { isTransitive = false }
    shade(project(":nms:v1_21_R4", configuration = "reobf")) { isTransitive = false }
    shade(project(":nms:v1_21_R5", configuration = "reobf")) { isTransitive = false }
    shade(project(":nms:v1_21_R6", configuration = "reobf")) { isTransitive = false }
    shade(project(":nms:v1_21_R7", configuration = "reobf")) { isTransitive = false }
    shade(project(":nms:v26_R1")) { isTransitive = false }

    compileOnly("io.lumine:Mythic-Dist:5.11.2")
    compileOnly("io.github.arcaneplugins:levelledmobs-plugin:4.0.3.1")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.alessiodp.parties:parties-bukkit:3.2.16")
    compileOnly("io.github.toxicity188:BetterHud-standard-api:1.14.1")
    compileOnly("io.github.toxicity188:BetterHud-bukkit-api:1.14.1")
    compileOnly("io.github.toxicity188:bettermodel-bukkit-api:3.0.0")
    compileOnly("net.citizensnpcs:citizens-main:2.0.42-SNAPSHOT")
    compileOnly("com.github.SkriptLang:Skript:2.15.0")
}

val pluginVersion = version.toString()
val pluginGroup = group.toString()
val shadeConfig = configurations.shade.get()
val dependenciesContent: List<String> = libs.bundles.library.download.map {
    it.map(Any::toString)
}.get()
val copyDirectory = rootProject.layout.buildDirectory.dir("libs")

interface FsInjected {
    @get:Inject val fs: FileSystemOperations
}
val copyShadowJar by tasks.registering {
    val injected = objects.newInstance<FsInjected>()
    val archiveFile = tasks.shadowJar.flatMap { it.archiveFile }
    val copyName = "${rootProject.name}-$pluginVersion.jar"
    val copyDir = copyDirectory
    doLast {
        injected.fs.copy {
            from(archiveFile)
            rename { copyName }
            into(copyDir)
        }
    }
}

tasks {
    jar {
        from(rootProject.file("LICENSE"))
        finalizedBy(shadowJar)
    }
    shadowJar {
        configurations = listOf(shadeConfig)
        manifest {
            attributes(
                "paperweight-mappings-namespace" to "spigot",
                "Version" to pluginVersion,
                "Author" to "toxicity188",
                "Url" to "https://github.com/toxicity188/BetterHealthBar",
                "Created-By" to "Gradle $gradle",
                "Build-Jdk" to "${System.getProperty("java.vendor")} ${System.getProperty("java.version")}",
                "Build-OS" to "${System.getProperty("os.arch")} ${System.getProperty("os.name")}"
            )
        }
        archiveClassifier = ""
        dependencies {
            exclude(dependency("org.jetbrains:annotations:13.0"))
        }
        fun prefix(pattern: String) {
            relocate(pattern, "$pluginGroup.shaded.$pattern")
        }
        prefix("kotlin")
        prefix("org.bstats")
        finalizedBy(copyShadowJar)
    }
}

bukkitPluginYaml {
    main = "$pluginGroup.BetterHealthBarImpl"
    name = rootProject.name
    version = pluginVersion
    apiVersion = "1.20.6"
    author = "toxicity188"
    description = "Make mob has greater health bar!"
    website = "https://www.spigotmc.org/resources/116619/"
    foliaSupported = true
    libraries = dependenciesContent
    softDepend = listOf(
        "BetterHud",
        "BetterModel",
        "ModelEngine",
        "MythicMobs",
        "PlaceholderAPI",
        "Citizens",
        "Skript",
        "SkBee",
        "skript-placeholders",
        "skript-reflect",
        "LevelledMobs",
        "Parties"
    )
    commands.create("healthbar") {
        aliases = listOf("hb")
        description = "BetterHealthBar's reload command."
        usage = "/<command>"
    }
    permissions.create("betterhealthbar.reload") {
        description = "Access to reload command."
        default = Permission.Default.OP
    }
}