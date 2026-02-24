plugins {
    `java-library`
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19" apply false
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("org.jetbrains.dokka") version "1.9.20" //TODO set this to 2.0.0 when stable version is released.
}

val minecraft = "1.21.11"
val adventure = "4.26.1"
val platform = "4.4.1"
val targetJavaVersion = 21

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")
    group = "kr.toxicity.healthbar"
    version = "3.11.0"
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
        maven("https://mvn.lumine.io/repository/maven-public/") {
        content {
            includeGroup("io.lumine")
            includeGroup("com.ticxo.modelengine")
        }
    }
    }
    dependencies {
        implementation("org.bstats:bstats-bukkit:3.1.0")
        compileOnly("net.jodah:expiringmap:0.5.11")
        implementation("com.github.toxicity188:SharedPackets:1.0.0") {
            exclude("net.kyori")
        }
        testImplementation(kotlin("test"))
    }
    tasks {
        test {
            useJUnitPlatform()
        }
    }
    java {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
    kotlin {
        jvmToolchain(targetJavaVersion)
    }
}

fun Project.dependency(dependency: Any) = also {
    it.dependencies.compileOnly(dependency)
}
fun Project.spigot() = dependency("org.spigotmc:spigot-api:$minecraft-R0.1-SNAPSHOT")
    .dependency("net.kyori:adventure-api:$adventure")
    .dependency("net.kyori:adventure-text-minimessage:$adventure")
    .dependency("net.kyori:adventure-platform-bukkit:$platform")

fun Project.paper() = dependency("io.papermc.paper:paper-api:$minecraft-R0.1-SNAPSHOT")

val api = project("api").spigot()

fun getApiDependencyProject(name: String) = project(name).dependency(api)

val dist = getApiDependencyProject("dist").spigot()
    .dependency("io.lumine:Mythic-Dist:5.11.1")
    .dependency("io.github.arcaneplugins:levelledmobs-plugin:4.0.3.1")
    .dependency("me.clip:placeholderapi:2.11.7")
    .dependency("com.alessiodp.parties:parties-bukkit:3.2.16")
    .dependency("io.github.toxicity188:BetterHud-standard-api:1.14.0")
    .dependency("io.github.toxicity188:BetterHud-bukkit-api:1.14.0")
    .dependency("io.github.toxicity188:bettermodel:1.15.1")
    .dependency("net.citizensnpcs:citizens-main:2.0.41-SNAPSHOT")
    .dependency("com.github.SkriptLang:Skript:2.13.2")
    .dependency("net.objecthunter:exp4j:0.4.8")
    .also {
        it.tasks.processResources {
            filteringCharset = Charsets.UTF_8.name()
            val props = mapOf(
                "version" to project.version,
                "adventure" to adventure,
                "platform" to platform
            )
            inputs.properties(props)
            filesMatching("plugin.yml") {
                expand(props)
            }
        }
    }

fun getProject(name: String) = getApiDependencyProject(name).also {
    dist.dependencies {
        compileOnly(it)
    }
}

class NmsVersion(val name: String) {
    val project = getProject("nms:$name").also {
        it.apply(plugin = "io.papermc.paperweight.userdev")
    }
}

val nmsVersions = listOf(
    NmsVersion("v1_19_R3"),
    NmsVersion("v1_20_R1"),
    NmsVersion("v1_20_R2"),
    NmsVersion("v1_20_R3"),
    NmsVersion("v1_20_R4"),
    NmsVersion("v1_21_R1"),
    NmsVersion("v1_21_R2"),
    NmsVersion("v1_21_R3"),
    NmsVersion("v1_21_R4"),
    NmsVersion("v1_21_R5"),
    NmsVersion("v1_21_R6"),
    NmsVersion("v1_21_R7")
)

dependencies {
    implementation(api)
    implementation(dist)
    implementation(getProject("scheduler:standard").spigot())
    implementation(getProject("scheduler:folia").paper())
    implementation(getProject("bedrock:geyser").spigot().dependency("org.geysermc.geyser:api:2.9.2-SNAPSHOT"))
    implementation(getProject("bedrock:floodgate").spigot().dependency("org.geysermc.floodgate:api:2.2.5-SNAPSHOT"))
    implementation(getProject("modelengine:legacy").spigot().dependency("com.ticxo.modelengine:api:R3.2.0"))
    implementation(getProject("modelengine:current").spigot().dependency("com.ticxo.modelengine:ModelEngine:R4.0.9"))
    nmsVersions.forEach {
        implementation(project(":nms:${it.name}", configuration = "reobf"))
    }
}

val sourceJar by tasks.registering(Jar::class) {
    dependsOn(tasks.classes)
    fun getProjectSource(project: Project): Array<File> {
        return if (project.subprojects.isEmpty()) project.sourceSets.main.get().allSource.srcDirs.toTypedArray() else ArrayList<File>().apply {
            project.subprojects.forEach {
                addAll(getProjectSource(it))
            }
        }.toTypedArray()
    }
    archiveClassifier = "source"
    from(*getProjectSource(project))
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
val dokkaJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtmlMultiModule)
    archiveClassifier = "dokka"
    from(layout.buildDirectory.dir("dokka/htmlMultiModule").orNull?.asFile)
}

tasks {
    jar {
        dependsOn(clean)
        finalizedBy(shadowJar)
    }
    runServer {
        version(minecraft)
        pluginJars(fileTree("plugins"))
        downloadPlugins {
            hangar("PlaceholderAPI", "2.11.7")
            hangar("Skript", "2.13.2")
            hangar("ViaVersion", "5.6.0")
        }
    }
    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "spigot"
        }
        nmsVersions.forEach {
            dependsOn("nms:${it.name}:reobfJar")
        }
        archiveClassifier = ""
        fun prefix(pattern: String) {
            relocate(pattern, "${project.group}.shaded.$pattern")
        }
        prefix("kotlin")
        prefix("org.bstats")
        prefix("kr.toxicity.library")
        dependencies {
            exclude(dependency("org.jetbrains:annotations:26.0.2"))
        }
    }
    build {
        finalizedBy(sourceJar, dokkaJar)
    }
}
