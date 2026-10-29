plugins {
    alias(libs.plugins.conventions.standard)
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

val minecraft = property("minecraft_version").toString()

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(tasks.dokkaGenerate)
    archiveClassifier = "javadoc"
    from(layout.buildDirectory.dir("dokka/html").orNull?.asFile)
}

runPaper {
    disablePluginJarDetection()
}

tasks {
    runServer {
        minecraftVersion(minecraft)
        pluginJars(project(":dist").tasks.named<Jar>("shadowJar").flatMap { it.archiveFile })
        pluginJars(fileTree("plugins"))
        downloadPlugins {
            modrinth("placeholderapi", "2.12.3")
            hangar("Skript", "2.17.1")
            hangar("ViaVersion", "5.12.2")
        }
    }
    jar {
        enabled = false
    }
    build {
        finalizedBy(javadocJar)
    }
}
