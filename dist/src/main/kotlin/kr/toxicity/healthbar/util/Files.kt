package kr.toxicity.healthbar.util

import org.bukkit.configuration.ConfigurationSection
import java.io.File

fun File.subFolder(name: String) = File(this, name).apply {
    if (!exists()) mkdir()
}

fun File.requireExists() = apply {
    if (!exists()) throw RuntimeException("This file doesn't exist: $path")
}

fun File.forEachAllYaml(block: (File, String, ConfigurationSection) -> Unit) {
    fun getAll(file: File): List<File> {
        return if (file.isDirectory) {
            file.listFiles()?.map { subFile ->
                getAll(subFile)
            }?.sum() ?: ArrayList()
        } else {
            listOf(file)
        }
    }
    getAll(this).filter {
        it.extension == "yml"
    }.forEach {
        runWithHandleException("Unable to load this yml file: ${it.name}") {
            val yaml = it.toYaml()
            yaml.getKeys(false).forEach { key ->
                yaml.getConfigurationSection(key)?.let { section ->
                    block(it, key, section)
                }
            }
        }
    }
}