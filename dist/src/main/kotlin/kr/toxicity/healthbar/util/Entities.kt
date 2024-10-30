package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.api.component.WidthComponent
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import kr.toxicity.healthbar.api.nms.VirtualTextDisplay
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.version.MinecraftVersion
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

val ATTRIBUTE_MAX_HEALTH = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(if (MinecraftVersion.current >= MinecraftVersion.version1_21_2) "max_health" else "generic.max_health"))!!
val ATTRIBUTE_ARMOR = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(if (MinecraftVersion.current >= MinecraftVersion.version1_21_2) "armor" else "generic.armor"))!!

fun HealthBarCreateEvent.toEntityLocation(): Location {
    return entity.entity().location.apply {
        y += (PLUGIN.modelEngine().height(entity.entity()) ?: entity.entity().eyeHeight) + ConfigManagerImpl.defaultHeight()
        entity.mob()?.let {
            y += it.configuration().height()
        }
    }
}
fun HealthBarCreateEvent.createEntity(component: WidthComponent, layer: Int = 0): VirtualTextDisplay {
    return PLUGIN.nms().createTextDisplay(player.player(), toEntityLocation(), component.component.build()).apply {
        val scale = healthBar.scale()
        transformation(
            Vector(0.0, if (ConfigManagerImpl.useCoreShaders()) -(1 - scale.y) * 8192 / 40 else 0.0, layer.toDouble() / 4000),
            scale
        )
    }
}

val LivingEntity.armor
    get(): Double {
        var attribute = getAttribute(ATTRIBUTE_ARMOR)?.value ?: 0.0
        val inventory = equipment ?: return attribute
        fun add(itemStack: ItemStack?) {
            itemStack?.itemMeta?.attributeModifiers?.get(ATTRIBUTE_ARMOR)?.sumOf { v ->
                v.amount
            }?.let {
                attribute += it
            }
        }
        add(inventory.helmet)
        add(inventory.chestplate)
        add(inventory.leggings)
        add(inventory.boots)
        return attribute
    }