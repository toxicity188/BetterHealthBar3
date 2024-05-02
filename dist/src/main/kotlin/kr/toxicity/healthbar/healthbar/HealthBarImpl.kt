package kr.toxicity.healthbar.healthbar

import kr.toxicity.healthbar.api.entity.HealthBarEntity
import kr.toxicity.healthbar.api.healthbar.HealthBar
import kr.toxicity.healthbar.api.healthbar.HealthBarTrigger
import kr.toxicity.healthbar.api.layout.LayoutGroup
import kr.toxicity.healthbar.api.player.HealthBarPlayer
import kr.toxicity.healthbar.api.renderer.HealthBarRenderer
import kr.toxicity.healthbar.api.renderer.HealthBarRenderer.RenderResult
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.manager.LayoutManagerImpl
import kr.toxicity.healthbar.util.*
import org.bukkit.configuration.ConfigurationSection
import java.util.Collections
import java.util.EnumSet
import java.util.UUID

class HealthBarImpl(
    private val path: String,
    private val uuid: UUID,
    section: ConfigurationSection
): HealthBar {
    private val groups = section.getStringList("groups").ifEmpty {
        throw RuntimeException("'groups' list is empty.")
    }.map {
        LayoutManagerImpl.group(it).ifNull("Unable to find this layout: $it")
    }
    private val triggers = Collections.unmodifiableSet(EnumSet.copyOf(section.getStringList("triggers").ifEmpty {
        throw RuntimeException("'triggers' list is empty.")
    }.map {
        HealthBarTrigger.valueOf(it.uppercase())
    }))
    private val duration = section.getInt("duration", ConfigManagerImpl.defaultDuration())

    override fun path(): String = path
    override fun uuid(): UUID = uuid
    override fun groups(): List<LayoutGroup> = groups
    override fun triggers(): Set<HealthBarTrigger> = triggers

    override fun duration(): Int = duration

    override fun createRenderer(entity: HealthBarEntity): HealthBarRenderer {
        return Renderer(entity)
    }

    private inner class Renderer(
        private val entity: HealthBarEntity
    ): HealthBarRenderer {
        private var d = 0
        private val images = groups.map {
            it.images().map { image ->
                image.createImageRenderer(entity)
            }
        }.sum().toMutableList()

        override fun hasNext(): Boolean {
            return entity.entity().isValid && (duration < 0 || ++d <= duration)
        }

        override fun render(): RenderResult {
            var comp = EMPTY_WIDTH_COMPONENT
            images.removeIf {
                !it.hasNext()
            }
            var max = 0
            images.forEach {
                val render = it.render()
                val length = render.pixel + render.component.width
                if (max < length) max = length
                comp += render.pixel.toSpaceComponent() + render.component + (-length).toSpaceComponent()
            }
            return RenderResult(
                comp + max.toSpaceComponent(),
                entity.entity().location.apply {
                    y += entity.entity().eyeHeight + PLUGIN.modelEngine().getHeight(entity.entity()) + ConfigManagerImpl.defaultHeight()
                }
            )
        }

        override fun updateTick() {
            d = 0
        }
    }
}