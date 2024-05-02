package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.entity.HealthBarEntity
import kr.toxicity.healthbar.api.manager.PlaceholderManager
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.pack.PackResource
import org.bukkit.attribute.Attribute

object PlaceholderManagerImpl: PlaceholderManager, BetterHealthBerManager {

    override fun start() {
        PlaceholderContainer.NUMBER.addPlaceholder("health") { e: HealthBarEntity ->
            e.entity().health
        }
        PlaceholderContainer.NUMBER.addPlaceholder("health_percentage") { e: HealthBarEntity ->
            e.entity().health / e.entity().getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
        }
    }

    override fun reload(resource: PackResource) {

    }
}