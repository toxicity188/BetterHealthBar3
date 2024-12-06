package kr.toxicity.healthbar.healthbar

import kr.toxicity.healthbar.api.healthbar.GroupIndex
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import kr.toxicity.healthbar.api.layout.LayoutGroup
import kr.toxicity.healthbar.api.nms.VirtualTextDisplay
import kr.toxicity.healthbar.api.renderer.ImageRenderer
import kr.toxicity.healthbar.api.renderer.PixelRenderer
import kr.toxicity.healthbar.util.*
import org.bukkit.Location

class RenderedLayout(group: LayoutGroup, pair: HealthBarCreateEvent) {
    val group = group.group()
    val images = group.images().map {
        it.createImageRenderer(pair)
    }.toMutableList()
    val texts = group.texts().map {
        it.createRenderer(pair)
    }.toMutableList()

    fun createPool(data: HealthBarCreateEvent, indexes: Map<String, GroupIndex>) = RenderedEntityPool(data, indexes)

    inner class RenderedEntityPool(
        private val data: HealthBarCreateEvent,
        private val indexes: Map<String, GroupIndex>
    ) {
        private val imagesEntity = images.map {
            RenderedEntity(it)
        }.toMutableList()
        private val textsEntity = texts.map {
            RenderedEntity(it)
        }.toMutableList()

        fun displays() = ArrayList<VirtualTextDisplay>().apply {
            imagesEntity.forEach {
                it.entity?.let { e ->
                    add(e)
                }
            }
            textsEntity.forEach {
                it.entity?.let { e ->
                    add(e)
                }
            }
        }

        var max = 0

        fun update(): Boolean {
            imagesEntity.removeIf {
                !it.has()
            }
            textsEntity.removeIf {
                !it.has()
            }
            val imageMap = imagesEntity.filter {
                it.can()
            }
            val textMap = textsEntity.filter {
                it.can()
            }
            if (imageMap.isEmpty() && textMap.isEmpty()) return false
            val count = group?.let { s ->
                indexes[s]
            }?.next() ?: 0
            max = 0
            imageMap.forEach {
                it.update(count)
            }
            textMap.forEach {
                it.update(count)
            }
            return true
        }

        fun create(max: Int) {
            val imageMap = imagesEntity.filter {
                it.can()
            }
            val textMap = textsEntity.filter {
                it.can()
            }
            val loc = data.toEntityLocation()
            imageMap.forEach {
                it.create(max, loc)
            }
            textMap.forEach {
                it.create(max, loc)
            }
        }

        fun remove() {
            imagesEntity.forEach {
                it.remove()
            }
            textsEntity.forEach {
                it.remove()
            }
        }

        private inner class RenderedEntity(
            private val renderer: PixelRenderer
        ) {
            var entity: VirtualTextDisplay? = null
            var comp = EMPTY_PIXEL_COMPONENT

            fun remove() {
                entity?.remove()
            }

            fun can(): Boolean {
                val result = renderer.canRender()
                if (!result) {
                    entity?.remove()
                    entity = null
                }
                return result
            }

            fun has() = renderer.hasNext()

            fun create(max: Int, loc: Location) {
                val length = comp.pixel + comp.component.width
                val finalComp = comp.pixel.toSpaceComponent() + comp.component + (-length + max).toSpaceComponent() + NEW_LAYER
                entity = entity?.apply {
                    teleport(loc)
                    text(finalComp.component.build())
                    update()
                } ?: data.createEntity(finalComp, renderer.layer())
            }

            fun update(count: Int) {
                comp = renderer.render(count)
                val length = comp.pixel + comp.component.width
                if (renderer is ImageRenderer && renderer.isBackground && max < length) max = length
            }
        }
    }
}