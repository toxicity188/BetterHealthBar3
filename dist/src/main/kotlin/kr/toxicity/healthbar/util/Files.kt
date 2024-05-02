package kr.toxicity.healthbar.util

import org.bukkit.configuration.ConfigurationSection
import java.io.File

fun File.subFolder(name: String) = File(this, name).apply {
    if (!exists()) mkdir()
}

fun File.requireExists() = apply {
    if (!exists()) throw RuntimeException("This file doesn't exist: $path")
}

fun File.forEachAllYamlAsync(block: (File, String, ConfigurationSection) -> Unit) {
    fun getAll(file: File): List<File> {
        return if (file.isDirectory) {
            file.listFiles()?.map { subFile ->
                getAll(subFile)
            }?.sum() ?: ArrayList()
        } else {
            listOf(file)
        }
    }
    val list = getAll(this).filter {
        it.extension == "yml"
    }.mapNotNull {
        runCatching {
            val yaml = it.toYaml()
            val list = ArrayList<Pair<String, ConfigurationSection>>()
            yaml.getKeys(false).forEach {
                yaml.getConfigurationSection(it)?.let { section ->
                    list.add(it to section)
                }
            }
            if (list.isNotEmpty()) it to list else null
        }.getOrElse { e ->
            warn(
                "Unable to load this yml file: ${it.name}",
                "Reason: ${e.message}"
            )
            null
        }
    }
    if (list.isEmpty()) return
    list.map {
        {
            it.second.forEachAsync { pair ->
                block(it.first, pair.first, pair.second)
            }
        }
    }.forEachAsync {
        it()
    }
}