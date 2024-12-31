package kr.toxicity.healthbar.layout

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.toxicity.healthbar.api.layout.ImageLayout
import kr.toxicity.healthbar.api.layout.LayoutGroup
import kr.toxicity.healthbar.api.layout.TextLayout
import kr.toxicity.healthbar.manager.EncodeManager
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.*
import net.kyori.adventure.key.Key
import org.bukkit.configuration.ConfigurationSection

class LayoutGroupImpl(
    private val path: String,
    val name: String,
    section: ConfigurationSection
): LayoutGroup {

    var index = ADVENTURE_START_INT

    private val encodedName = encodeKey(EncodeManager.EncodeNamespace.FONT, "$name/images")
    private val imageKey = createAdventureKey(encodedName)
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

    fun build(resource: PackResource, count: Int) {
        val json = JsonArray()
        images.forEach {
            it.build(resource, count, json)
        }
        resource.font.add("$encodedName.json") {
            JsonObject().apply {
                add("providers", json)
            }.save()
        }
        texts.forEach {
            it.build(resource, count)
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