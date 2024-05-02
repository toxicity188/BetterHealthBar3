package kr.toxicity.healthbar.util

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

fun File.toYaml() = YamlConfiguration.loadConfiguration(this)
fun InputStream.toYaml() = InputStreamReader(this).buffered().use {
    YamlConfiguration.loadConfiguration(it)
}
fun ConfigurationSection.forEachSubConfiguration(block: (String, ConfigurationSection) -> Unit) {
    getKeys(false).forEach {
        getConfigurationSection(it)?.let { config ->
            block(it, config)
        }
    }
}
fun ConfigurationSection.forEachSubConfigurationIndexed(block: (Int, ConfigurationSection) -> Unit) {
    var i = 0
    getKeys(false).forEach {
        getConfigurationSection(it)?.let { config ->
            block(i++, config)
        }
    }
}