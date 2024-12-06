package kr.toxicity.healthbar.compatibility

import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.util.placeholder
import net.citizensnpcs.api.CitizensAPI
import java.util.function.Function

class CitizensCompatibility : Compatibility {
    override fun accept() {
        PlaceholderContainer.BOOL.addPlaceholder("citizens_npc", placeholder(0) {
            Function {
                CitizensAPI.getNPCRegistry().getByUniqueIdGlobal(it.entity.entity().uniqueId) != null
            }
        })
    }
}