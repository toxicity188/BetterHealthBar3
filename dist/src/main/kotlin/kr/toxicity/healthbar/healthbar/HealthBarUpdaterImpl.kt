package kr.toxicity.healthbar.healthbar

import kr.toxicity.healthbar.api.healthbar.HealthBarData
import kr.toxicity.healthbar.api.healthbar.HealthBarUpdater
import kr.toxicity.healthbar.api.healthbar.HealthBarUpdaterGroup
import kr.toxicity.healthbar.api.nms.VirtualTextDisplay
import kr.toxicity.healthbar.api.renderer.HealthBarRenderer
import kr.toxicity.healthbar.util.PLUGIN
import org.bukkit.util.Vector

class HealthBarUpdaterImpl(
    private val parent: HealthBarUpdaterGroupImpl,
    data: HealthBarData,
    private val renderer: HealthBarRenderer
): HealthBarUpdater {
    private val display = renderer().render().run {
        val player = data.player.player()
        PLUGIN.nms().createTextDisplay(player, location, component.component.build()).apply {
            val scale = data.healthBar.scale()
            transformation(
                Vector(0.0, -(1 - scale.y) * 8192 / 40 ,0.0),
                scale
            )
        }
    }

    override fun parent(): HealthBarUpdaterGroup = parent
    override fun display(): VirtualTextDisplay = display
    override fun renderer(): HealthBarRenderer = renderer

    override fun updateTick() {
        renderer.updateTick()
    }

    override fun remove() {
        display.remove()
    }

    override fun update(): Boolean {
        if (!renderer().hasNext()) {
            display.remove()
            return false
        } else {
            val render = renderer().render()
            display.teleport(render.location)
            display.text(render.component.component.build())
            return true
        }
    }
}