package kr.toxicity.healthbar.compatibility

import io.github.arcaneplugins.levelledmobs.LevelledMobs
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.util.placeholder
import java.util.function.Function

class LevelledMobsCompatibility : Compatibility {
    override fun accept() {
        PlaceholderContainer.NUMBER.addPlaceholder("levelledmobs_level", placeholder(0) {
            Function { data ->
                LevelledMobs.instance.levelManager.getLevelOfMob(data.entity.entity())
            }
        })
    }
}