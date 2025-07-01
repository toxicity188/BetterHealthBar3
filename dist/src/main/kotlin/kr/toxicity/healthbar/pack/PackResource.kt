package kr.toxicity.healthbar.pack

import kr.toxicity.healthbar.util.*
import java.util.*


class PackResource {
    class Builder(val dir: String, val supplier: () -> ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Builder

            return dir == other.dir
        }

        override fun hashCode(): Int {
            return dir.hashCode()
        }
    }

    class ListBuilder {
        private val set = Collections.synchronizedSet(HashSet<Builder>())

        fun add(dir: String, supplier: () -> ByteArray) {
            set.add(Builder(dir, supplier))
        }
        fun forEachAsync(block: (Builder) -> Unit) = set.toList().forEachAsync(block)
    }

    val dataFolder = DATA_FOLDER

    val merge = ListBuilder()
    val textures = ListBuilder().apply {
        PLUGIN.getResource("splitter.png")?.buffered()?.let {
            add("splitter.png") {
                it.use { stream ->
                    stream.readAllBytes()
                }
            }
        }
    }
    val font = ListBuilder().apply {
        add("space.json") {
            jsonObjectOf("providers" to jsonArrayOf(
                jsonObjectOf(
                    "type" to "bitmap",
                    "file" to "$NAMESPACE:splitter.png",
                    "ascent" to -9999,
                    "height" to -2,
                    "chars" to jsonArrayOf(NEW_LAYER_INT.parseChar())
                ),
                jsonObjectOf(
                    "type" to "space",
                    "advances" to jsonObjectOf(*(-8192..8192).map { i ->
                        (ADVENTURE_START_INT + i).parseChar() to i
                    }.toTypedArray())
                )
            )).save()
        }
    }
}