package kr.toxicity.healthbar.pack

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.toxicity.healthbar.util.*
import java.util.Collections


class PackResource {
    class Builder(val dir: String, val supplier: () -> ByteArray)

    class ListBuilder {
        private val list = Collections.synchronizedList(ArrayList<Builder>())

        fun add(dir: String, supplier: () -> ByteArray) {
            list.add(Builder(dir, supplier))
        }
        fun forEachAsync(block: (Builder) -> Unit) = list.forEachAsync(block)
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