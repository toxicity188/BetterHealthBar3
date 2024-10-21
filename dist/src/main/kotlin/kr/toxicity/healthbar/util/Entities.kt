package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.api.component.WidthComponent
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import kr.toxicity.healthbar.api.nms.VirtualTextDisplay
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import org.bukkit.Location
import org.bukkit.util.Vector

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