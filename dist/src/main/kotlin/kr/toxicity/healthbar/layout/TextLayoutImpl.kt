package kr.toxicity.healthbar.layout

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.toxicity.healthbar.api.component.PixelComponent
import kr.toxicity.healthbar.api.component.WidthComponent
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import kr.toxicity.healthbar.api.layout.TextLayout
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.api.placeholder.PlaceholderOption
import kr.toxicity.healthbar.api.renderer.TextRenderer
import kr.toxicity.healthbar.api.text.TextAlign
import kr.toxicity.healthbar.data.BitmapData
import kr.toxicity.healthbar.manager.EncodeManager
import kr.toxicity.healthbar.manager.TextManagerImpl
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.configuration.ConfigurationSection
import java.util.Collections
import java.util.function.Function
import kotlin.math.roundToInt

class TextLayoutImpl(
    private val parent: LayoutGroupImpl,
    private val name: String,
    layer: Int,
    section: ConfigurationSection
): TextLayout, LayoutImpl(layer, section) {
    companion object {
        private val defaultWidth = WidthKey(Key.key("minecraft", "default"), 0)
    }
    private val property = section.getConfigurationSection("properties")?.let {
        PlaceholderOption.of(it)
    } ?: PlaceholderOption.EMPTY
    private val text = section.getString("text").ifNull("Unable to find 'text' configuration.").run {
        TextManagerImpl.text(this).ifNull("Unable to find this text: $this")
    }
    private val height = (text.height().toDouble() * scale()).roundToInt().toHeight()
    private val textWidth = Collections.unmodifiableMap(HashMap<Int, Int>().apply {
        val div = height.toDouble() / text.height().toDouble()
        text.chatWidth().forEach {
            put(it.key, (it.value * div).roundToInt())
        }
    })
    private val align = section.getString("align").ifNull("Unable to find 'align' command.").run {
        TextAlign.valueOf(uppercase())
    }
    private val duration = section.getInt("duration", - 1)
    private val keys = ArrayList<WidthKey>()
    private val pattern = PlaceholderContainer.toString(
        property,
        section.getString("pattern").ifNull("Unable to find 'pattern' command.")
    )

    override fun charWidth(): Map<Int, Int> = textWidth
    override fun align(): TextAlign = align
    override fun property(): PlaceholderOption.Property = property
    override fun pattern(): Function<HealthBarCreateEvent, Component> = pattern
    override fun height(): Int = height

    private class WidthKey(
        val key: Key,
        val x: Int,
    )

    fun build(resource: PackResource, count: Int) {
        val dataList = ArrayList<JsonData>()
        val fileParent = "${parent.name}/text/${layer()}"
        text.bitmap().forEachIndexed { index, textBitmap ->
            val fileName = "${encodeKey(EncodeManager.EncodeNamespace.TEXTURES, "$fileParent/${index + 1}")}.png"
            dataList.add(JsonData(
                "$NAMESPACE:$fileName",
                textBitmap.array
            ))
            resource.textures.add(fileName) {
                textBitmap.image.withOpacity(layer()).toByteArray()
            }
        }

        val map = HashMap<BitmapData, WidthKey>()
        for (i in 0..<count) {
            val y = y() + groupY() * i
            val keyName = encodeKey(EncodeManager.EncodeNamespace.FONT, "${parent.name}/$name/${i + 1}")
            keys.add(map.computeIfAbsent(BitmapData(keyName, y, height)) {
                resource.font.add("$keyName.json") {
                    JsonObject().apply {
                        add("providers", JsonArray().apply {
                            add(JsonObject().apply {
                                addProperty("type", "space")
                                add("advances", JsonObject().apply {
                                    addProperty(" ", 4)
                                })
                            })
                            dataList.forEach {
                                add(JsonObject().apply {
                                    addProperty("type", "bitmap")
                                    addProperty("file", it.file)
                                    addProperty("ascent", y.toAscent())
                                    addProperty("height", height)
                                    add("chars", it.chars)
                                })
                            }
                        })
                    }.save()
                }
                WidthKey(createAdventureKey(keyName), x() + groupX() * i)
            })
        }
    }

    override fun createRenderer(pair: HealthBarCreateEvent): TextRenderer {
        return Renderer(pair)
    }

    private inner class Renderer(
        private val pair: HealthBarCreateEvent
    ): TextRenderer {
        private var d = 0
        override fun hasNext(): Boolean {
            return duration < 0 || ++d <= duration
        }

        override fun canRender(): Boolean {
            return condition().apply(pair)
        }

        override fun layer(): Int = this@TextLayoutImpl.layer()

        override fun render(groupCount: Int): PixelComponent {
            val key = if (keys.isNotEmpty()) keys[groupCount.coerceAtMost(keys.lastIndex)] else defaultWidth
            val target = pattern.apply(pair)
            fun length(component: Component): Int {
                return ((component as? TextComponent)?.let {
                    val s = it.style()
                    var i = 0
                    if (s.hasDecoration(TextDecoration.BOLD)) i++
                    if (s.hasDecoration(TextDecoration.ITALIC)) i++
                    it.content().codePoints().map { c ->
                        if (c == ' '.code) 4 else (textWidth[c] ?: 0) + 1 + i
                    }.sum()
                } ?: 0) + component.children().sumOf {
                    length(it)
                }
            }
            val component = WidthComponent(
                length(target),
                Component.text().append(target.font(key.key))
            )
            return component.toPixelComponent(key.x + when (align) {
                TextAlign.LEFT -> 0
                TextAlign.CENTER -> -component.width / 2
                TextAlign.RIGHT -> -component.width
            })
        }

    }

    private class JsonData(
        val file: String,
        val chars: JsonArray
    )
}