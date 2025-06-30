package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.listener.HealthBarListener
import kr.toxicity.healthbar.api.manager.ListenerManager
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.api.placeholder.PlaceholderOption
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.ATTRIBUTE_MAX_HEALTH
import kr.toxicity.healthbar.util.armor
import kr.toxicity.healthbar.util.ifNull
import org.bukkit.configuration.ConfigurationSection
import java.util.function.Function

object ListenerManagerImpl : ListenerManager, BetterHealthBerManager {

    private val listenerMap = hashMapOf<String, (ConfigurationSection) -> HealthBarListener>(
        "health" to {
            HealthBarListener {
                it.entity.entity().health / it.entity.entity().getAttribute(ATTRIBUTE_MAX_HEALTH)!!.value
            }
        },
        "health_before" to {
            HealthBarListener {
                (it.entity.entity().health + it.entity.entity().lastDamage) / it.entity.entity().getAttribute(ATTRIBUTE_MAX_HEALTH)!!.value
            }
        },
        "absorption" to {
            HealthBarListener {
                it.entity.entity().absorptionAmount / it.entity.entity().getAttribute(ATTRIBUTE_MAX_HEALTH)!!.value
            }
        },
        "armor" to {
            HealthBarListener {
                it.entity.entity().armor / 20.0
            }
        },
        "placeholder" to {
            val property = it.getConfigurationSection("property")?.let { section ->
                PlaceholderOption.of(section)
            } ?: PlaceholderOption.EMPTY
            val valueConfig = it.getString("value").ifNull { "config section 'value' not found." }
            val maxConfig = it.getString("max").ifNull { "config section 'max' not found." }
            val value = PlaceholderContainer.parse(property, valueConfig)
                .assertNumber {
                    "This placeholder is not a number: $valueConfig"
                }
            val max = PlaceholderContainer.parse(property, maxConfig)
                .assertNumber {
                    "This placeholder is not a number: $maxConfig"
                }
            HealthBarListener placeholder@ { event ->
                val v = value.value(event) ?: return@placeholder -1.0
                val m = max.value(event) ?: return@placeholder -1.0
                ((v as Number).toDouble() / (m as Number).toDouble())
                    .coerceAtLeast(0.0)
                    .coerceAtMost(1.0)
            }
        }
    )

    override fun reload(resource: PackResource) {
    }

    override fun addListener(name: String, listenerFunction: Function<ConfigurationSection, HealthBarListener>) {
        listenerMap[name] = {
            listenerFunction.apply(it)
        }
    }

    override fun build(section: ConfigurationSection): HealthBarListener {
        val clazz = section.getString("class").ifNull { "Unable to find 'class' value." }
        return listenerMap[clazz].ifNull { "Unable to find this listener: $clazz" }(section)
    }
}