package kr.toxicity.healthbar.healthbar

import kr.toxicity.healthbar.api.component.WidthComponent
import kr.toxicity.healthbar.api.condition.HealthBarCondition
import kr.toxicity.healthbar.api.healthbar.GroupIndex
import kr.toxicity.healthbar.api.healthbar.HealthBar
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import kr.toxicity.healthbar.api.trigger.HealthBarTriggerType
import kr.toxicity.healthbar.api.layout.LayoutGroup
import kr.toxicity.healthbar.api.nms.VirtualTextDisplay
import kr.toxicity.healthbar.api.renderer.HealthBarRenderer
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.manager.LayoutManagerImpl
import kr.toxicity.healthbar.util.*
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.Vector
import java.util.ArrayList
import java.util.Collections
import java.util.EnumSet
import java.util.UUID

class HealthBarImpl(
    private val path: String,
    private val s: String,
    private val uuid: UUID,
    section: ConfigurationSection
): HealthBar {
    private val groups = section.getStringList("groups").ifEmpty {
        throw RuntimeException("'groups' list is empty.")
    }.map {
        LayoutManagerImpl.name(it).ifNull("Unable to find this layout: $it")
    }
    private val triggers = Collections.unmodifiableSet(EnumSet.copyOf(section.getStringList("triggers").ifEmpty {
        throw RuntimeException("'triggers' list is empty.")
    }.map {
        HealthBarTriggerType.valueOf(it.uppercase())
    }))
    private val duration = section.getInt("duration", ConfigManagerImpl.defaultDuration())
    private val conditions = section.toCondition()
    private val isDefault = section.getBoolean("default")
    private val applicableTypes = section.getStringList("applicable-types").toSet()
    private val scale = section.getConfigurationSection("scale")?.let {
        Vector(
            it.getDouble("x", 1.0),
            it.getDouble("y", 1.0),
            it.getDouble("z", 1.0)
        )
    } ?: Vector(1, 1, 1)
    private val shadowRadius = section.getDouble("shadow-radius", 0.0).toFloat()
    private val shadowStrength = section.getDouble("shadow-strength", 1.0).toFloat()

    override fun path(): String = path
    override fun uuid(): UUID = uuid
    override fun applicableTypes(): Set<String> = applicableTypes
    override fun groups(): List<LayoutGroup> = groups
    override fun triggers(): Set<HealthBarTriggerType> = triggers
    override fun condition(): HealthBarCondition = conditions
    override fun isDefault(): Boolean = isDefault
    override fun scale(): Vector = Vector(scale.x, scale.y, scale.z)
    override fun shadowRadius(): Float = shadowRadius
    override fun shadowStrength(): Float = shadowStrength

    override fun duration(): Int = duration

    override fun createRenderer(pair: HealthBarCreateEvent): HealthBarRenderer {
        return if (ConfigManagerImpl.useCoreShaders()) SingleRenderer(pair) else MultiRenderer(pair)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HealthBarImpl

        return s == other.s
    }

    override fun hashCode(): Int {
        return s.hashCode()
    }

    private abstract inner class AbstractRenderer(val event: HealthBarCreateEvent) : HealthBarRenderer {
        val indexes = groups.mapNotNull {
            it.group()
        }.associateWith {
            GroupIndex()
        }

        val render = groups.map {
            RenderedLayout(it,  event)
        }.toMutableList()

        var d = 0

        override fun hasNext(): Boolean {
            if (!event.check()) return false
            val entity = event.entity.entity()
            val player = event.player.player()
            return entity.isValid && entity.world.uid == player.world.uid && player.location.distance(entity.location) < ConfigManagerImpl.lookDistance() && (duration < 0 || ++d <= duration)
        }
        override fun canRender(): Boolean {
            return conditions.apply(event)
        }
        override fun updateTick() {
            d = 0
        }
    }
    private inner class MultiRenderer(
        pair: HealthBarCreateEvent
    ) : AbstractRenderer(pair) {

        private val displays = render.map {
            it.createPool(pair, indexes)
        }.toMutableList()

        override fun work(): Boolean {
            if (!hasNext() || displays.isEmpty()) {
                displays.forEach {
                    it.remove()
                }
                return false
            } else {
                indexes.values.forEach {
                    it.clear()
                }
                var result = false
                var max = 0
                val pool = ArrayList<RenderedLayout.RenderedEntityPool>()
                displays.forEach {
                    if (it.update()) {
                        result = true
                        if (max < it.max) max = it.max
                        pool.add(it)
                    }
                }
                pool.forEach {
                    it.create(max)
                }
                return result
            }
        }

        override fun displays(): List<VirtualTextDisplay> = displays.map {
            it.displays()
        }.sum()

        override fun stop() {
            displays.forEach {
                it.remove()
            }
        }
    }
    private inner class SingleRenderer(
        pair: HealthBarCreateEvent
    ) : AbstractRenderer(pair) {

        private val display = pair.createEntity(render())

        private fun removeUnused() {
            indexes.values.forEach {
                it.clear()
            }
            render.removeIf {
                it.images.removeIf { element ->
                    !element.hasNext()
                }
                it.texts.removeIf { element ->
                    !element.hasNext()
                }
                it.images.isEmpty() && it.texts.isEmpty()
            }
        }

        override fun stop() {
            display.remove()
        }

        override fun displays(): List<VirtualTextDisplay> = listOf(display)

        override fun work(): Boolean {
            if (!hasNext() || render.isEmpty()) {
                display.remove()
                return false
            } else {
                removeUnused()
                val render = render()
                display.teleport(event.toEntityLocation())
                display.text(render.component.build())
                display.update()
                return true
            }
        }

        private fun render(): WidthComponent {
            var comp = EMPTY_WIDTH_COMPONENT
            var max = 0
            render.forEach {
                val imageRender = it.images.filter { r ->
                    r.canRender()
                }
                val textRender = it.texts.filter { r ->
                    r.canRender()
                }
                if (imageRender.isEmpty() && textRender.isEmpty()) return@forEach
                val next = it.group?.let { s ->
                    indexes[s]
                }?.next() ?: 0
                imageRender.forEach { image ->
                    val render = image.render(next)
                    val length = render.pixel + render.component.width
                    if (image.isBackground && max < length) max = length
                    comp += render.pixel.toSpaceComponent() + render.component + (-length).toSpaceComponent() + NEW_LAYER
                }
                textRender.forEach { text ->
                    val render = text.render(next)
                    val length = render.pixel + render.component.width
                    comp += render.pixel.toSpaceComponent() + render.component + (-length).toSpaceComponent() + NEW_LAYER
                }
            }
            return comp + (max).toSpaceComponent()
        }
    }


}