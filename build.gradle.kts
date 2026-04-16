plugins {
    alias(libs.plugins.conventions.standard)
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

val minecraft = property("minecraft_version").toString()

val javadocJar by tasks.registering(Jar::class) {
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
            hangar("PlaceholderAPI", "2.12.2")
            hangar("Skript", "2.15.0")
            hangar("ViaVersion", "5.8.1")
        }
    }
    jar {
        enabled = false
    }
    build {
        finalizedBy(javadocJar)
    }
}
