package kr.toxicity.healthbar.layout

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.toxicity.healthbar.api.layout.ImageLayout
import kr.toxicity.healthbar.api.layout.LayoutGroup
import kr.toxicity.healthbar.api.layout.TextLayout
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.*
import net.kyori.adventure.key.Key
import org.bukkit.configuration.ConfigurationSection
import kotlin.math.max
import kotlin.math.min

class LayoutGroupImpl(
    private val path: String,
    val name: String,
    section: ConfigurationSection
): LayoutGroup {

    var index = ADVENTURE_START_INT

    private val imageKey = Key.key(NAMESPACE, "$name/images")
    private val group = section.getString("group")

    private var i = 0

    private val images = ArrayList<ImageLayoutImpl>().apply {
        section.getConfigurationSection("images")?.forEachSubConfiguration { _, configurationSection ->
            add(
                ImageLayoutImpl(
                    this@LayoutGroupImpl,
                    ++i,
                    configurationSection
                )
            )
        }
    }
    private val texts = ArrayList<TextLayoutImpl>().apply {
        section.getConfigurationSection("texts")?.forEachSubConfiguration { s, configurationSection ->
            add(
                TextLayoutImpl(
                    this@LayoutGroupImpl,
                    s,
                    ++i,
                    configurationSection
                )
            )
        }
    }
    val min get() = if (ConfigManagerImpl.useCoreShaders()) 0 else min(
        -max(
            images.maxOfOrNull {
                it.y() - (it.image().images().maxOfOrNull { image ->
                    image.image.image.height
                } ?: 0)
            } ?: 0,
            texts.maxOfOrNull {
                it.y() - it.height()
            } ?: 0
        ),
        0
    )

    fun build(resource: PackResource, min: Int, count: Int) {
        val json = JsonArray()
        images.forEach {
            it.build(resource, min, count, json)
        }
        resource.font.add("$name/images.json") {
            JsonObject().apply {
                add("providers", json)
            }.save()
        }
        texts.forEach {
            it.build(resource, min, count)
        }
    }

    override fun group(): String? = group
    override fun path(): String = path
    override fun images(): List<ImageLayout> = images
    override fun imageKey(): Key = imageKey
    override fun texts(): List<TextLayout> = texts

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LayoutGroupImpl

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}