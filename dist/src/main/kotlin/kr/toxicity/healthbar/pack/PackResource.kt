package kr.toxicity.healthbar.pack

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.toxicity.healthbar.api.pack.PackType
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.util.*
import kr.toxicity.healthbar.version.MinecraftVersion
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

    val merge = ListBuilder().apply {
        if (ConfigManagerImpl.packType() != PackType.NONE && ConfigManagerImpl.createPackMcmeta()) {
            PLUGIN.getResource("icon.png")?.buffered()?.let {
                add("pack.png") {
                    it.use { stream ->
                        stream.readAllBytes()
                    }
                }
            }
            add("pack.mcmeta") {
                JsonObject().apply {
                    add("pack", JsonObject().apply {
                        addProperty("pack_format", MinecraftVersion.current.packVersion())
                        addProperty("description", "BetterHealthBar's resource pack.")
                    })
                }.save()
            }
        }
    }
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
            JsonObject().apply {
                add("providers", JsonArray().apply {
                    add(JsonObject().apply {
                        addProperty("type", "bitmap")
                        addProperty("file", "$NAMESPACE:splitter.png")
                        addProperty("ascent", -9999)
                        addProperty("height", -2)
                        add("chars", JsonArray().apply {
                            add(NEW_LAYER_INT.parseChar())
                        })
                    })
                    add(JsonObject().apply {
                        addProperty("type", "space")
                        add("advances", JsonObject().apply {
                            for (i in -8192..8192) {
                                addProperty((ADVENTURE_START_INT + i).parseChar(), i)
                            }
                        })
                    })
                })
            }.save()
        }
    }
}