package kr.toxicity.healthbar.layout

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kr.toxicity.healthbar.api.layout.ImageLayout
import kr.toxicity.healthbar.api.layout.LayoutGroup
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.ADVENTURE_START_INT
import kr.toxicity.healthbar.util.NAMESPACE
import kr.toxicity.healthbar.util.forEachSubConfigurationIndexed
import kr.toxicity.healthbar.util.save
import net.kyori.adventure.key.Key
import org.bukkit.configuration.ConfigurationSection

class LayoutGroupImpl(
    private val path: String,
    val name: String,
    section: ConfigurationSection
): LayoutGroup {

    var index = ADVENTURE_START_INT

    private val imageKey = Key.key(NAMESPACE, "$name/images")
    private val group = section.getString("group")

    private val images = ArrayList<ImageLayoutImpl>().apply {
        section.getConfigurationSection("images")?.forEachSubConfigurationIndexed { i, configurationSection ->
            add(
                ImageLayoutImpl(
                    this@LayoutGroupImpl,
                    i + 1,
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
        resource.font.add("$name/images.json") {
            JsonObject().apply {
                add("providers", json)
            }.save()
        }
    }

    override fun group(): String? = group
    override fun path(): String = path
    override fun images(): List<ImageLayout> = images
    override fun imageKey(): Key = imageKey

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