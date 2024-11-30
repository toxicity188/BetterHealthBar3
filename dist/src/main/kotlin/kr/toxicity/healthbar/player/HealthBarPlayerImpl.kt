package kr.toxicity.healthbar.player

import kr.toxicity.healthbar.api.entity.HealthBarEntity
import kr.toxicity.healthbar.api.healthbar.HealthBar
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import kr.toxicity.healthbar.api.healthbar.HealthBarUpdaterGroup
import kr.toxicity.healthbar.api.player.HealthBarPlayer
import kr.toxicity.healthbar.api.trigger.HealthBarTrigger
import kr.toxicity.healthbar.healthbar.HealthBarUpdaterGroupImpl
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.util.PLUGIN
import kr.toxicity.healthbar.util.asyncTaskTimer
import kr.toxicity.healthbar.util.call
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import java.util.*

class HealthBarPlayerImpl(
    private val player: Player
): HealthBarPlayer {
    override fun player(): Player = player
    override fun compareTo(other: HealthBarPlayer): Int = player.uniqueId.compareTo(other.player().uniqueId)

    private val updaterMap = HashMap<UUID, HealthBarUpdaterGroup>()
    private val task = asyncTaskTimer(1, 1) {
        synchronized(updaterMap) {
            updaterMap.values.removeIf {
                !it.update()
            }
        }
    }

    init {
        PLUGIN.nms().inject(this)
    }

    override fun uninject() {
        task.cancel()
        PLUGIN.nms().uninject(this)
    }

    override fun clear() {
        synchronized(updaterMap) {
            updaterMap.values.removeIf {
                it.remove()
                true
            }
        }
    }

    override fun updaterMap(): MutableMap<UUID, HealthBarUpdaterGroup> = synchronized(updaterMap) {
        updaterMap
    }

    override fun showHealthBar(healthBar: HealthBar, trigger: HealthBarTrigger, entity: HealthBarEntity) {
        if (ConfigManagerImpl.blacklistEntityType().contains(entity.entity().type)) return
        entity.mob()?.let {
            if (it.configuration().blacklist()) return
        }
        val data = HealthBarCreateEvent(
            healthBar,
            trigger,
            this,
            entity
        ).addPredicate {
            when {
                ConfigManagerImpl.disableToInvulnerableMob() && entity.entity().isInvulnerable -> false
                ConfigManagerImpl.disableToInvisibleMob() && (entity.entity().isInvisible || entity.entity().hasPotionEffect(PotionEffectType.INVISIBILITY)) -> false
                !ConfigManagerImpl.showMeHealthBar() && player.uniqueId == entity.entity().uniqueId -> false
                else -> true
            }
        }.addPredicate {
            healthBar.condition().apply(it)
        }
        if (!data.check() || !data.call()) return
        synchronized(updaterMap) {
            updaterMap.computeIfAbsent(entity.entity().uniqueId) {
                HealthBarUpdaterGroupImpl(this, entity)
            }.addHealthBar(data)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HealthBarPlayerImpl

        return player.uniqueId == other.player.uniqueId
    }

    override fun hashCode(): Int {
        return player.uniqueId.hashCode()
    }
}