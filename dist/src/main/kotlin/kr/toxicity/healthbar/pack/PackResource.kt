package kr.toxicity.healthbar.pack

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.toxicity.healthbar.util.*
import java.util.Collections


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

    val textures = ListBuilder()
    val font = ListBuilder().apply {
        add("space.json") {
            JsonObject().apply {
                add("providers", JsonArray().apply {
                    add(JsonObject().apply {
                        addProperty("type", "space")
                        add("advances", JsonObject().apply {
                            (-8192..8192).forEach { i ->
                                addProperty((ADVENTURE_START_INT + i).parseChar(), i)
                            }
                        })
                    })
                })
            }.save()
        }
    }
}