package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.compatibility.MythicActiveMob
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import kr.toxicity.healthbar.api.manager.PlaceholderManager
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.ATTRIBUTE_MAX_HEALTH
import kr.toxicity.healthbar.util.armor
import kr.toxicity.healthbar.util.placeholder
import kr.toxicity.healthbar.util.warn
import kr.toxicity.healthbar.version.MinecraftVersion
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import java.util.function.Function
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType
import io.lumine.mythic.bukkit.MythicBukkit

object PlaceholderManagerImpl : PlaceholderManager, BetterHealthBerManager {

    override fun start() {
        PlaceholderContainer.NUMBER.run {
            addPlaceholder("health") { e: HealthBarCreateEvent ->
                e.entity.entity().health
            }
            addPlaceholder("max_health") { e: HealthBarCreateEvent ->
                e.entity.entity().getAttribute(ATTRIBUTE_MAX_HEALTH)!!.value
            }
            addPlaceholder("health_percentage") { e: HealthBarCreateEvent ->
                e.entity.entity().health / e.entity.entity().getAttribute(ATTRIBUTE_MAX_HEALTH)!!.value
            }
            addPlaceholder("absorption") { e: HealthBarCreateEvent ->
                e.entity.entity().absorptionAmount
            }
            addPlaceholder("armor") { e: HealthBarCreateEvent ->
                e.entity.entity().armor
            }
        }
        PlaceholderContainer.STRING.run {
            addPlaceholder("entity_type") { e: HealthBarCreateEvent ->
                e.entity.entity().type.toString().lowercase()
            }
            addPlaceholder("entity_name") { e: HealthBarCreateEvent ->
                e.entity.entity().name
            }
            addPlaceholder("entity_lmlevel") {
                e: HealthBarCreateEvent ->
                val entity = e.entity.entity()

                // Option 1: If BukkitValues are mirrored as metadata
                val meta = entity.getMetadata("levelledmobs:level").firstOrNull()
                if (meta != null && meta.value() is Number) {
                    return@addPlaceholder meta.value().toString()
                }

                // Option 2: If LevelledMobs uses PersistentDataContainer
                if (entity is LivingEntity) {
                    val container = entity.persistentDataContainer
                    val key = NamespacedKey("levelledmobs", "level")
                    val level = container.get(key, PersistentDataType.INTEGER)
                    if (level != null) {
                        return@addPlaceholder level.toString()
                    }
                }

                // Fallback if nothing found
                "?"
            }
            addPlaceholder("level") { e: HealthBarCreateEvent ->
                val entity = e.entity.entity()
                var mlevel = "?"

                // Try MythicMobs level first
                if (entity is LivingEntity) {
                    mlevel = MythicBukkit.inst()
                        .mobManager
                        .getMythicMobInstance(entity)   // <- no .entity()
                        ?.level
                        ?.toInt()
                        ?.toString() ?: "?"
                }

                // Fallback: player XP level
                if (entity is Player && mlevel == "?") {
                    return@addPlaceholder entity.level.toString()   // smart cast in Kotlin
                }

                return@addPlaceholder mlevel
            }
        }
        PlaceholderContainer.BOOL.run {
            addPlaceholder("has_potion_effect", placeholder(1) {
                if (MinecraftVersion.current >= MinecraftVersion.version1_20_3) {
                    Registry.EFFECT.get(NamespacedKey.minecraft(it[0]))
                } else {
                    @Suppress("DEPRECATION")
                    PotionEffectType.getByName(it[0])
                }?.let { type ->
                    Function { pair: HealthBarCreateEvent ->
                        pair.entity.entity().hasPotionEffect(type)
                    }
                } ?: run {
                    warn("Unable to find this potion effect: ${it[0]}")
                    Function { _: HealthBarCreateEvent ->
                        false
                    }
                }
            })
            addPlaceholder("is_player") { e: HealthBarCreateEvent ->
                e.entity.entity() is Player
            }
            addPlaceholder("is_mythic_mob") { e: HealthBarCreateEvent ->
                e.entity.mob() is MythicActiveMob
            }
            addPlaceholder("is_vanilla_mob") { e: HealthBarCreateEvent ->
                e.entity.mob() == null
            }
        }
    }

    override fun reload(resource: PackResource) {

    }
}