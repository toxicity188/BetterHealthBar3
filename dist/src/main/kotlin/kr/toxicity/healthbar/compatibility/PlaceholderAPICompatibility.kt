package kr.toxicity.healthbar.compatibility

import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.util.placeholder
import me.clip.placeholderapi.PlaceholderAPI
import java.util.function.Function

class PlaceholderAPICompatibility : Compatibility {
    override fun accept() {
        PlaceholderContainer.STRING.addPlaceholder("papi", placeholder(1) {
            val papi = if (it[0].startsWith('%')) it[0] else "%${it[0]}%"
            Function { d ->
                PlaceholderAPI.setPlaceholders(d.player.player(), papi)
            }
        })
    }
}