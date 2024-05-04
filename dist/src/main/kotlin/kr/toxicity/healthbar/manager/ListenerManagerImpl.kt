package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.listener.HealthBarListener
import kr.toxicity.healthbar.api.manager.ListenerManager
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.ifNull
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.ConfigurationSection
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

object ListenerManagerImpl: ListenerManager, BetterHealthBerManager {

    private val listenerMap = ConcurrentHashMap<String, (ConfigurationSection) -> HealthBarListener>().apply {
        put("health") {
            HealthBarListener {
                it.entity.entity().health / it.entity.entity().getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            }
        }
        put("health_before") {
            HealthBarListener {
                (it.entity.entity().health + it.entity.entity().lastDamage) / it.entity.entity().getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            }
        }
        put("absorption") {
            HealthBarListener {
                (it.entity.entity().absorptionAmount) / it.entity.entity().getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            }
        }
    }

    override fun reload(resource: PackResource) {
    }

    override fun addListener(name: String, listenerFunction: Function<ConfigurationSection, HealthBarListener>) {
        listenerMap[name] = {
            listenerFunction.apply(it)
        }
    }

    override fun build(section: ConfigurationSection): HealthBarListener {
        val clazz = section.getString("class").ifNull("Unable to find 'class' value.")
        return listenerMap[clazz].ifNull("Unable to find this listener: $clazz")(section)
    }
}