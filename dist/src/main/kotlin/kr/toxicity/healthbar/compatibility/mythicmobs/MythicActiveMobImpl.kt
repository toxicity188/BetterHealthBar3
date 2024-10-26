package kr.toxicity.healthbar.compatibility.mythicmobs

import io.lumine.mythic.core.mobs.ActiveMob
import kr.toxicity.healthbar.api.compatibility.MythicActiveMob
import kr.toxicity.healthbar.api.mob.MobConfiguration

class MythicActiveMobImpl(
    private val mob: ActiveMob,
    private val configuration: MobConfiguration,
): MythicActiveMob {
    override fun id(): String = mob.type.internalName
    override fun handle(): Any = mob
    override fun configuration(): MobConfiguration = configuration
}