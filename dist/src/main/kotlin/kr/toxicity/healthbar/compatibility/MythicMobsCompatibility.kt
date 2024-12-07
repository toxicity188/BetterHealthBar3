package kr.toxicity.healthbar.compatibility

import io.lumine.mythic.api.skills.placeholders.PlaceholderString
import io.lumine.mythic.bukkit.MythicBukkit
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.compatibility.mythicmobs.MythicMobsMobProvider
import kr.toxicity.healthbar.manager.MobManagerImpl
import kr.toxicity.healthbar.util.placeholder
import java.util.function.Function

class MythicMobsCompatibility : Compatibility {
    override fun accept() {
        MobManagerImpl.addProvider(MythicMobsMobProvider())

        PlaceholderContainer.STRING.addPlaceholder("mythicmobs", placeholder(1) {
            val s = PlaceholderString.of(it[0])
            Function { data ->
                MythicBukkit.inst().mobManager.getMythicMobInstance(data.entity.entity())?.let { a ->
                    s.get(a)
                } ?: "<none>"
            }
        })
        PlaceholderContainer.NUMBER.addPlaceholder("mythicmobs_level", placeholder(0) {
            Function { data ->
                MythicBukkit.inst().mobManager.getMythicMobInstance(data.entity.entity())?.level ?: 0
            }
        })
    }
}