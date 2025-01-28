package kr.toxicity.healthbar.compatibility

import org.bukkit.entity.Player
import java.util.function.Function
import me.clip.placeholderapi.PlaceholderAPI
import kr.toxicity.healthbar.util.placeholder
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer

class PlaceholderAPICompatibility : Compatibility {
    override fun accept() {
        PlaceholderContainer.STRING.addPlaceholder("papi", placeholder(1) {
            val papi = if (it[0].startsWith('%')) it[0] else "%${it[0]}%"
            Function { d ->
                PlaceholderAPI.setPlaceholders(d.player.player(), papi)
            }
        })

        PlaceholderContainer.STRING.addPlaceholder("papi_entity", placeholder(1) {
            val papi = if (it[0].startsWith('%')) it[0] else "%${it[0]}%"
            Function { d ->
                PlaceholderAPI.setPlaceholders(d.entity.entity() as? Player ?: return@Function "", papi)
            }
        })
    }
}