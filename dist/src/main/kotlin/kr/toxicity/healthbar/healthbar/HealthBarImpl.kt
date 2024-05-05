package kr.toxicity.healthbar.healthbar

import kr.toxicity.healthbar.api.condition.HealthBarCondition
import kr.toxicity.healthbar.api.healthbar.GroupIndex
import kr.toxicity.healthbar.api.healthbar.HealthBar
import kr.toxicity.healthbar.api.healthbar.HealthBarData
import kr.toxicity.healthbar.api.trigger.HealthBarTriggerType
import kr.toxicity.healthbar.api.layout.LayoutGroup
import kr.toxicity.healthbar.api.renderer.HealthBarRenderer
import kr.toxicity.healthbar.api.renderer.HealthBarRenderer.RenderResult
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.manager.LayoutManagerImpl
import kr.toxicity.healthbar.util.*
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.Vector
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

    override fun path(): String = path
    override fun uuid(): UUID = uuid
    override fun applicableTypes(): Set<String> = applicableTypes
    override fun groups(): List<LayoutGroup> = groups
    override fun triggers(): Set<HealthBarTriggerType> = triggers
    override fun condition(): HealthBarCondition = conditions
    override fun isDefault(): Boolean = isDefault
    override fun scale(): Vector = Vector(scale.x, scale.y, scale.z)

    override fun duration(): Int = duration

    override fun createRenderer(pair: HealthBarData): HealthBarRenderer {
        return Renderer(pair)
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

    private class RenderedLayout(group: LayoutGroup, pair: HealthBarData) {
        val group = group.group()
        val images = group.images().map {
            it.createImageRenderer(pair)
        }.toMutableList()
        val texts = group.texts().map {
            it.createRenderer(pair)
        }.toMutableList()
    }

    private inner class Renderer(
        private val pair: HealthBarData
    ): HealthBarRenderer {
        private var d = 0

        private val indexes = groups.mapNotNull {
            it.group()
        }.associateWith {
            GroupIndex()
        }

        private val render = groups.map {
            RenderedLayout(it, pair)
        }.toMutableList()

        override fun hasNext(): Boolean {
            val entity = pair.entity.entity()
            val player = pair.player.player()
            return entity.isValid && entity.world.uid == player.world.uid && player.location.distance(entity.location) < ConfigManagerImpl.lookDistance() && (duration < 0 || ++d <= duration)
        }

        override fun canRender(): Boolean {
            return conditions.apply(pair)
        }

        override fun render(): RenderResult {
            var comp = EMPTY_WIDTH_COMPONENT
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
            var max = 0
            render.forEach {
                val index = it.group?.let { s ->
                    indexes[s]
                }
                val imageRender = it.images.filter { r ->
                    r.canRender()
                }
                val textRender = it.texts.filter { r ->
                    r.canRender()
                }
                if (imageRender.isEmpty() && textRender.isEmpty()) return@forEach
                val next = index?.next() ?: 0
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
            val entity = pair.entity
            return RenderResult(
                comp + (max).toSpaceComponent(),
                entity.entity().location.apply {
                    y += (PLUGIN.modelEngine().height(pair.entity.entity()) ?: pair.entity.entity().eyeHeight) + ConfigManagerImpl.defaultHeight()
                    entity.mob()?.let {
                        y += it.configuration().height()
                    }
                }
            )
        }

        override fun updateTick() {
            d = 0
        }
    }


}