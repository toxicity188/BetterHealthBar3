package kr.toxicity.healthbar.manager

import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import kr.toxicity.healthbar.api.manager.TextManager
import kr.toxicity.healthbar.api.text.HealthBarText
import kr.toxicity.healthbar.api.text.TextBitmap
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.text.HealthBarTextImpl
import kr.toxicity.healthbar.util.*
import java.awt.Font
import java.awt.font.FontRenderContext
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Collections
import java.util.TreeMap
import java.util.TreeSet
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

object TextManagerImpl : TextManager, BetterHealthBerManager {

    private val frc = FontRenderContext(null, true, true)
    private const val SPLIT_SIZE = 16

    private lateinit var default: HealthBarTextImpl
    private val textMap = ConcurrentHashMap<String, HealthBarTextImpl>()

    override fun text(name: String): HealthBarText? = textMap[name]

    override fun start() {
        val charWidth = HashMap<Int, Int>()
        PLUGIN.getResource("width.txt")?.let {
            InputStreamReader(it, StandardCharsets.UTF_8).buffered().use { reader ->
                reader.readLines().forEach { line ->
                    val split = line.split(':')
                    if (split.size < 2) return@forEach
                    runCatching {
                        charWidth[split[0].toInt(16)] = split[1].toInt()
                    }
                }
            }
        }
        default = HealthBarTextImpl(
            "default",
            Collections.unmodifiableMap(charWidth),
            emptyList(),
            12
        )
    }

    override fun reload(resource: PackResource) {
        textMap.clear()
        textMap["default"] = default
        val fonts = resource.dataFolder.subFolder("fonts")
        resource.dataFolder.subFolder("texts").forEachAllYaml { file, s, configurationSection ->
            runWithHandleException("Unable to read this text: $s in ${file.path}") {
                val font = Font.createFont(Font.TRUETYPE_FONT, File(fonts, configurationSection.getString("file").ifNull("Unable to find 'file' configuration.").replace('/', File.separatorChar)).apply {
                    if (!exists()) throw RuntimeException("Unable to find this font: $path")
                }).deriveFont(configurationSection.getInt("scale", 16).coerceAtLeast(1).toFloat())
                val parse = parseFont(file.path, font)
                textMap.putSync("text", s) {
                    parse
                }
            }
        }
    }

    private class CharImage(
        val char: Int,
        val image: BufferedImage
    ): Comparable<CharImage> {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CharImage

            return char == other.char
        }

        override fun hashCode(): Int {
            return char.hashCode()
        }

        override fun compareTo(other: CharImage): Int {
            return char.compareTo(other.char)
        }
    }

    private fun parseFont(path: String, font: Font): HealthBarTextImpl {
        val imageMap = TreeMap<Int, MutableSet<CharImage>>()
        val charWidth = HashMap<Int, Int>()

        fun register(char: Int, image: BufferedImage) {
            synchronized(imageMap) {
                imageMap.computeIfAbsent(image.width) {
                    TreeSet()
                }.add(CharImage(char, image))
            }
            synchronized(charWidth) {
                charWidth[char] = image.width
            }
        }

        val height = (font.size.toDouble() * 1.4).roundToInt()

        (0..0x10FFFF).filter {
            font.canDisplay(it)
        }.forEachAsync {
            BufferedImage(font.size, height, BufferedImage.TYPE_INT_ARGB).apply {
                createGraphics().run {
                    fill(font.createGlyphVector(frc, it.parseChar()).getOutline(0F, font.size.toFloat()))
                    dispose()
                }
            }.removeEmptyWidth()?.let { image ->
                register(it, image.image)
            }
        }

        val bitMapList = ArrayList<TextBitmap>()
        imageMap.forEach {
            fun save(image: List<CharImage>) {
                val array = JsonArray()
                val sb = StringBuilder()
                val target = BufferedImage(it.key * image.size.coerceAtMost(SPLIT_SIZE), height * (((image.size - 1) / SPLIT_SIZE).coerceAtLeast(0) + 1), BufferedImage.TYPE_INT_ARGB)
                target.createGraphics().run {
                    image.forEachIndexed { index, charImage ->
                        drawImage(charImage.image, it.key * (index % SPLIT_SIZE), height * (index / SPLIT_SIZE), null)
                        sb.appendCodePoint(charImage.char)
                        if ((index + 1) % SPLIT_SIZE == 0) {
                            array.add(JsonPrimitive(sb.toString()))
                            sb.setLength(0)
                        }
                    }
                    dispose()
                }
                if (sb.isNotEmpty()) {
                    array.add(JsonPrimitive(sb.toString()))
                    sb.setLength(0)
                }
                synchronized(bitMapList) {
                    bitMapList.add(TextBitmap(target, array))
                }
            }
            it.value.toList().split(SPLIT_SIZE * SPLIT_SIZE).forEachAsync { list ->
                if (list.size % SPLIT_SIZE == 0 || list.size < SPLIT_SIZE) {
                    save(list)
                } else {
                    val sub = list.split(SPLIT_SIZE)
                    save(sub.subList(0, sub.lastIndex).sum())
                    save(sub.last())
                }
            }
        }
        return HealthBarTextImpl(
            path,
            Collections.unmodifiableMap(charWidth),
            Collections.unmodifiableList(bitMapList),
            height
        )
    }
}