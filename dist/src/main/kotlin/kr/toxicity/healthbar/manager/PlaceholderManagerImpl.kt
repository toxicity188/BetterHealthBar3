package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.healthbar.HealthBarPair
import kr.toxicity.healthbar.api.manager.PlaceholderManager
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.pack.PackResource
import org.bukkit.attribute.Attribute

object PlaceholderManagerImpl: PlaceholderManager, BetterHealthBerManager {

    override fun start() {
        PlaceholderContainer.NUMBER.addPlaceholder("health") { e: HealthBarPair ->
            e.entity.entity().health
        }
        PlaceholderContainer.NUMBER.addPlaceholder("health_percentage") { e: HealthBarPair ->
            e.entity.entity().health / e.entity.entity().getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
        }
        PlaceholderContainer.NUMBER.addPlaceholder("absorption") { e: HealthBarPair ->
            e.entity.entity().absorptionAmount
        }
    }

    override fun reload(resource: PackResource) {

    }
}