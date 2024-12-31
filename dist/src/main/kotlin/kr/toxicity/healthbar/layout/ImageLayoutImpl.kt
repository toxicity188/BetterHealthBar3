package kr.toxicity.healthbar.layout

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.toxicity.healthbar.api.component.PixelComponent
import kr.toxicity.healthbar.api.component.WidthComponent
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import kr.toxicity.healthbar.api.image.HealthBarImage
import kr.toxicity.healthbar.api.layout.ImageLayout
import kr.toxicity.healthbar.api.listener.HealthBarListener
import kr.toxicity.healthbar.api.renderer.ImageRenderer
import kr.toxicity.healthbar.data.BitmapData
import kr.toxicity.healthbar.manager.EncodeManager
import kr.toxicity.healthbar.manager.ImageManagerImpl
import kr.toxicity.healthbar.manager.ListenerManagerImpl
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.*
import net.kyori.adventure.text.Component
import org.bukkit.configuration.ConfigurationSection
import kotlin.math.roundToInt

class ImageLayoutImpl(
    private val parent: LayoutGroupImpl,
    layer: Int,
    section: ConfigurationSection
): ImageLayout, LayoutImpl(layer, section) {
    private val image = section.getString("image").ifNull("Unable to find 'image' configuration.").run {
        ImageManagerImpl.image(this).ifNull("Unable to find this image: $this")
    }
    private val components = ArrayList<List<PixelComponent>>()
    private val listener = section.getConfigurationSection("listener")?.let {
        ListenerManagerImpl.build(it)
    } ?: HealthBarListener.INVALID
    private val duration = section.getInt("duration", - 1)
    private val background = section.getBoolean("background", true)

    override fun image(): HealthBarImage = image
    override fun listener(): HealthBarListener = listener
    override fun duration(): Int = duration

    fun build(resource: PackResource, count: Int, jsonArray: JsonArray) {
        val componentMap = HashMap<BitmapData, WidthComponent>()
        image.images().forEach {
            val list = ArrayList<PixelComponent>()
            val dir = "${parent.name}/image/${layer()}/${it.name}".encodeFile(EncodeManager.EncodeNamespace.TEXTURES)
            resource.textures.add(dir) {
                it.image.image.withOpacity(layer()).toByteArray()
            }
            val newHeight = (it.image.image.height.toDouble() * scale()).roundToInt()
            val div = newHeight.toDouble() / it.image.image.height.toDouble()
            for (i in 0..<count) {
                val y = y() + groupY() * i
                list.add(componentMap.computeIfAbsent(BitmapData(dir, y, newHeight)) { _ ->
                    val component = parent.index++.parseChar()
                    jsonArray.add(JsonObject().apply {
                        addProperty("type", "bitmap")
                        addProperty("file", "$NAMESPACE:$dir")
                        addProperty("ascent", y.toAscent())
                        addProperty("height", newHeight.toHeight())
                        add("chars", JsonArray().apply {
                            add(component)
                        })
                    })
                    WidthComponent((it.image.image.width.toDouble() * div).roundToInt(), Component.text()
                        .font(parent.imageKey())
                        .content(component)
                        .append(NEGATIVE_ONE_SPACE_COMPONENT.component)
                    )
                }.toPixelComponent(x() + groupX() * i))
            }
            components.add(list)
        }
    }

    override fun createImageRenderer(pair: HealthBarCreateEvent): ImageRenderer {
        return Renderer(pair)
    }

    private inner class Renderer(
        private val pair: HealthBarCreateEvent
    ): ImageRenderer {
        private var next = 0
        private var d = 0

        override fun hasNext(): Boolean {
            return duration < 0 || ++d <= duration
        }

        override fun canRender(): Boolean {
            return condition().apply(pair)
        }

        override fun layer(): Int = this@ImageLayoutImpl.layer()

        override fun isBackground(): Boolean = background

        override fun render(count: Int): PixelComponent {
            val listen = listener.value(pair).run {
                if (isNaN()) 0.0 else this
            }
            val list = if (listen >= 0) {
                components[(listen * components.lastIndex).roundToInt().coerceAtMost(components.lastIndex)]
            } else {
                components[next++ % components.size]
            }
            return list[count.coerceAtMost(list.lastIndex)]
        }
    }
}