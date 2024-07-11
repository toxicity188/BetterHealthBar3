package kr.toxicity.healthbar.healthbar

import kr.toxicity.healthbar.api.healthbar.GroupIndex
import kr.toxicity.healthbar.api.healthbar.HealthBarData
import kr.toxicity.healthbar.api.layout.LayoutGroup
import kr.toxicity.healthbar.api.nms.VirtualTextDisplay
import kr.toxicity.healthbar.api.renderer.PixelRenderer
import kr.toxicity.healthbar.util.*
import org.bukkit.Location

class RenderedLayout(group: LayoutGroup, pair: HealthBarData) {
    val group = group.group()
    val images = group.images().map {
        it.createImageRenderer(pair)
    }.toMutableList()
    val texts = group.texts().map {
        it.createRenderer(pair)
    }.toMutableList()

    fun createPool(data: HealthBarData, indexes: Map<String, GroupIndex>) = RenderedEntityPool(data, indexes)

    inner class RenderedEntityPool(
        private val data: HealthBarData,
        private val indexes: Map<String, GroupIndex>
    ) {
        private val imagesEntity = images.map {
            RenderedEntity(it)
        }
        private val textsEntity = images.map {
            RenderedEntity(it)
        }

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

        private fun count(): Int {
            val index = group?.let { s ->
                indexes[s]
            }
            return index?.next() ?: 0
        }

        var max = 0

        fun update(): Boolean {
            val count = count()
            val loc = data.toEntityLocation()
            var result = false
            max = 0
            val array = ArrayList<() -> Unit>()
            imagesEntity.forEach {
                if (it.update(count)) {
                    array.add {
                        it.create(loc)
                    }
                    result = true
                }
            }
            textsEntity.forEach {
                if (it.update(count)) {
                    array.add {
                        it.create(loc)
                    }
                    result = true
                }
            }
            array.forEach {
                it()
            }
            return result
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
            var comp = EMPTY_WIDTH_COMPONENT

            fun remove() {
                entity?.remove()
            }

            fun create(loc: Location) {
                val finalComp = EMPTY_WIDTH_COMPONENT + comp + NEW_LAYER + (-comp.width + max).toSpaceComponent()
                entity = (entity?.apply {
                    text(finalComp.component.build())
                } ?: data.createEntity(finalComp, renderer.layer())).apply {
                    teleport(loc)
                }
            }

            fun update(count: Int): Boolean {
                return if (renderer.canRender()) {
                    comp = renderer.render(count).component
                    if (max < comp.width) max = comp.width
                    true
                } else {
                    entity?.remove()
                    false
                }
            }
        }
    }
}