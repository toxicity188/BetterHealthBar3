package kr.toxicity.healthbar.compatibility

import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.util.placeholder
import net.citizensnpcs.api.CitizensAPI
import java.util.function.Function

class CitizensCompatibility: Compatibility {
    override fun accept() {
        PlaceholderContainer.BOOL.addPlaceholder("citizens_npc", placeholder(0) {
            Function {
                val entity = it.entity.entity()
                CitizensAPI.getNPCRegistries().any { r ->
                    r.isNPC(entity)
                }
            }
        })
    }
}