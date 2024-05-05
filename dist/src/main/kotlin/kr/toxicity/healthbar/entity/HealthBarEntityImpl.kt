package kr.toxicity.healthbar.entity

import kr.toxicity.healthbar.api.entity.HealthBarEntity
import kr.toxicity.healthbar.api.mob.HealthBarMob
import kr.toxicity.healthbar.manager.MobManagerImpl
import org.bukkit.entity.LivingEntity

class HealthBarEntityImpl(
    private val entity: LivingEntity
): HealthBarEntity {
    private val mob = MobManagerImpl.provide(entity)

    override fun entity(): LivingEntity = entity
    override fun mob(): HealthBarMob? = mob

    override fun compareTo(other: HealthBarEntity): Int {
        return entity.uniqueId.compareTo(other.entity().uniqueId)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HealthBarEntityImpl

        return entity.uniqueId == other.entity.uniqueId
    }

    override fun hashCode(): Int {
        return entity.uniqueId.hashCode()
    }
}