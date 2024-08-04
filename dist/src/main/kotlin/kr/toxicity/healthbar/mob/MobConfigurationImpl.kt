package kr.toxicity.healthbar.mob

import kr.toxicity.healthbar.api.healthbar.HealthBar
import kr.toxicity.healthbar.api.mob.MobConfiguration
import kr.toxicity.healthbar.manager.HealthBarManagerImpl
import kr.toxicity.healthbar.util.ifNull
import org.bukkit.configuration.ConfigurationSection

class MobConfigurationImpl(
    private val path: String,
    section: ConfigurationSection
): MobConfiguration {
    private val types = section.getStringList("type").toSet()
    private val height = section.getDouble("height")
    private val blacklist = section.getBoolean("blacklist")
    private val ignoreDefault = section.getBoolean("ignore-default")
    private val healthBars = section.getStringList("healthbars").map {
        HealthBarManagerImpl.healthBar(it).ifNull("Unable to find this health bar: $it")
    }.toSet()

    override fun types(): Set<String> = types
    override fun path(): String = path
    override fun height(): Double = height
    override fun blacklist(): Boolean = blacklist
    override fun ignoreDefault(): Boolean = ignoreDefault
    override fun healthBars(): Set<HealthBar> = healthBars
}