package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.healthbar.HealthBarPair
import kr.toxicity.healthbar.api.manager.PlaceholderManager
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.placeholder
import kr.toxicity.healthbar.version.MinecraftVersion
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffectType
import java.util.function.Function

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
        PlaceholderContainer.BOOL.addPlaceholder("has_potion_effect", placeholder(1) {
            val type = if (MinecraftVersion.current >= MinecraftVersion.version1_20_3) {
                Registry.EFFECT.get(NamespacedKey.minecraft(it[0]))
            } else {
                @Suppress("DEPRECATION")
                PotionEffectType.getByName(it[0])
            } ?: throw RuntimeException("Unable to find that effect type: ${it[0]}")
            Function { pair ->
                pair.entity.entity().hasPotionEffect(type)
            }
        })
    }

    override fun reload(resource: PackResource) {

    }
}