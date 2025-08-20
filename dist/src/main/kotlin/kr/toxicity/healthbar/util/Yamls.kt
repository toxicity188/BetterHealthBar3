package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.api.condition.HealthBarCondition
import kr.toxicity.healthbar.api.condition.HealthBarOperation
import kr.toxicity.healthbar.equation.TEquation
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
fun ConfigurationSection.getAsEquation(key: String) = getString(key)?.let {
    TEquation(it)
} ?: TEquation.zero
fun ConfigurationSection.toCondition(): HealthBarCondition = run {
    var condition = HealthBarCondition.TRUE
    getConfigurationSection("conditions")?.forEachSubConfiguration { _, configurationSection ->
        val old = condition
        var get = HealthBarOperation.of(configurationSection)
        if (configurationSection.getBoolean("not")) get = get.not()
        condition = when (val gate = configurationSection.getString("gate")?.lowercase() ?: "and") {
            "and" -> HealthBarCondition {
                old.apply(it) && get.apply(it)
            }
            "or" -> HealthBarCondition {
                old.apply(it) || get.apply(it)
            }
            else -> throw RuntimeException("Unsupported gate: $gate")
        }
    }
    condition
} ?: HealthBarCondition.TRUE