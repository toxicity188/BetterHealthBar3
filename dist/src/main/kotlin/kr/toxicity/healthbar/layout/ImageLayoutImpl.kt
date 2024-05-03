package kr.toxicity.healthbar.layout

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.toxicity.healthbar.api.component.PixelComponent
import kr.toxicity.healthbar.api.component.WidthComponent
import kr.toxicity.healthbar.api.entity.HealthBarEntity
import kr.toxicity.healthbar.api.healthbar.HealthBarPair
import kr.toxicity.healthbar.api.image.HealthBarImage
import kr.toxicity.healthbar.api.layout.ImageLayout
import kr.toxicity.healthbar.api.listener.HealthBarListener
import kr.toxicity.healthbar.api.renderer.ImageRenderer
import kr.toxicity.healthbar.manager.ImageManagerImpl
import kr.toxicity.healthbar.manager.ListenerManagerImpl
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.*
import net.kyori.adventure.text.Component
import org.bukkit.configuration.ConfigurationSection
import kotlin.math.roundToInt

class ImageLayoutImpl(
    parent: LayoutGroupImpl,
    resource: PackResource,
    layer: Int,
    section: ConfigurationSection
): ImageLayout, LayoutImpl(layer, section) {
    private val image = section.getString("image").ifNull("Unable to find 'image' configuration.").run {
        ImageManagerImpl.image(this).ifNull("Unable to find this image: $this")
    }
    private val components = ArrayList<PixelComponent>()
    private val listener = section.getConfigurationSection("listener")?.let {
        ListenerManagerImpl.build(it)
    } ?: HealthBarListener.INVALID
    private val duration = section.getInt("duration", - 1)

    override fun image(): HealthBarImage = image
    override fun components(): MutableList<PixelComponent> = components
    override fun listener(): HealthBarListener = listener
    override fun duration(): Int = duration

    init {
        image.images().forEach {
            val dir = "${parent.name}/$layer/${it.name}"
            resource.textures.add(dir) {
                it.image.image.withOpacity(layer).toByteArray()
            }
            parent.jsonArray.get()?.let { array ->
                val component = (parent.index++).parseChar()

                val newHeight = (it.image.image.height * scale()).roundToInt()
                val div = newHeight / it.image.image.height.toDouble()

                array.add(JsonObject().apply {
                    addProperty("type", "bitmap")
                    addProperty("file", "$NAMESPACE:$dir")
                    addProperty("ascent", y().toAscent())
                    addProperty("height", newHeight)
                    add("chars", JsonArray().apply {
                        add(component)
                    })
                })
                components.add(PixelComponent(x(), WidthComponent((it.image.image.width.toDouble() * div).roundToInt(), Component.text()
                    .font(parent.imageKey())
                    .content(component)
                    .append(NEGATIVE_ONE_SPACE_COMPONENT.component)
                )))
            }
        }
    }
    override fun iterator(): MutableIterator<PixelComponent> = components.iterator()

    override fun createImageRenderer(pair: HealthBarPair): ImageRenderer {
        return Renderer(pair)
    }

    private inner class Renderer(
        private val pair: HealthBarPair
    ): ImageRenderer {
        private var next = 0
        private var d = 0

        override fun hasNext(): Boolean {
            return duration < 0 || ++d <= duration
        }

        override fun render(): PixelComponent {
            return if (condition().apply(pair)) {
                val listen = listener.value(pair).run {
                    if (isNaN()) 0.0 else this
                }
                if (listen >= 0) {
                    components[(listen * components.lastIndex).roundToInt().coerceAtMost(components.lastIndex)]
                } else {
                    components[(next++) % components.size]
                }
            } else EMPTY_PIXEL_COMPONENT
        }
    }
}