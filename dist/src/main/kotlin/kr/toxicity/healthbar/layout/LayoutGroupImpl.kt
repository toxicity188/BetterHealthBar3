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
import java.lang.ref.WeakReference

class LayoutGroupImpl(
    private val path: String,
    val name: String,
    resource: PackResource,
    section: ConfigurationSection
): LayoutGroup {

    val jsonArray = WeakReference(JsonArray())
    var index = ADVENTURE_START_INT

    private val imageKey = Key.key(NAMESPACE, "$name/images")

    private val images = ArrayList<ImageLayoutImpl>().apply {
        section.getConfigurationSection("images")?.forEachSubConfigurationIndexed { i, configurationSection ->
            add(
                ImageLayoutImpl(
                    this@LayoutGroupImpl,
                    resource,
                    i + 1,
                    configurationSection
                )
            )
        }
    }

    init {
        jsonArray.get()?.let {
            resource.font.add("$name/images.json") {
                JsonObject().apply {
                    add("providers", it)
                }.save()
            }
        }
    }

    override fun path(): String = path
    override fun images(): List<ImageLayout> = images
    override fun imageKey(): Key = imageKey
}