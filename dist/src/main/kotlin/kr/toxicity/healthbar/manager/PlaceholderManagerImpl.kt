package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.healthbar.HealthBarData
import kr.toxicity.healthbar.api.manager.PlaceholderManager
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.placeholder
import kr.toxicity.healthbar.util.warn
import kr.toxicity.healthbar.version.MinecraftVersion
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.Tag
import org.bukkit.attribute.Attribute
import org.bukkit.potion.PotionEffectType
import java.util.function.Function

object PlaceholderManagerImpl: PlaceholderManager, BetterHealthBerManager {

    override fun start() {
        PlaceholderContainer.NUMBER.run {
            addPlaceholder("health") { e: HealthBarData ->
                e.entity.entity().health
            }
            addPlaceholder("max_health") { e: HealthBarData ->
                e.entity.entity().getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            }
            addPlaceholder("health_percentage") { e: HealthBarData ->
                e.entity.entity().health / e.entity.entity().getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            }
            addPlaceholder("absorption") { e: HealthBarData ->
                e.entity.entity().absorptionAmount
            }
        }
        PlaceholderContainer.STRING.run {
            addPlaceholder("entity_type") { e: HealthBarData ->
                e.entity.entity().type.toString().lowercase()
            }
            addPlaceholder("entity_name") { e: HealthBarData ->
                e.entity.entity().name
            }
        }
        PlaceholderContainer.BOOL.addPlaceholder("has_potion_effect", placeholder(1) {
            if (MinecraftVersion.current >= MinecraftVersion.version1_20_3) {
                Registry.EFFECT.get(NamespacedKey.minecraft(it[0]))
            } else {
                @Suppress("DEPRECATION")
                PotionEffectType.getByName(it[0])
            }?.let { type ->
                Function { pair: HealthBarData ->
                    pair.entity.entity().hasPotionEffect(type)
                }
            } ?: run {
                warn("Unable to find this potion effect: ${it[0]}")
                Function { _: HealthBarData ->
                    false
                }
            }
        })
    }

    override fun reload(resource: PackResource) {

    }
}