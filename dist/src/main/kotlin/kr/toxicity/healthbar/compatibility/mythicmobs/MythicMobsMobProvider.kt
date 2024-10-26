package kr.toxicity.healthbar.compatibility.mythicmobs

import io.lumine.mythic.bukkit.MythicBukkit
import kr.toxicity.healthbar.api.mob.HealthBarMob
import kr.toxicity.healthbar.api.mob.MobProvider
import kr.toxicity.healthbar.manager.MobManagerImpl
import org.bukkit.entity.LivingEntity

class MythicMobsMobProvider : MobProvider {
    override fun provide(entity: LivingEntity): HealthBarMob? {
        return MythicBukkit.inst().mobManager.getMythicMobInstance(entity)?.let {
            MythicActiveMobImpl(it, MobManagerImpl.configuration(it.mobType) ?: return null)
        }
    }
}