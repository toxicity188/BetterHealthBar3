package kr.toxicity.healthbar.healthbar

import kr.toxicity.healthbar.api.entity.HealthBarEntity
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import kr.toxicity.healthbar.api.healthbar.HealthBarUpdater
import kr.toxicity.healthbar.api.healthbar.HealthBarUpdaterGroup
import kr.toxicity.healthbar.api.player.HealthBarPlayer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class HealthBarUpdaterGroupImpl(
    private val player: HealthBarPlayer,
    private val entity: HealthBarEntity,
): HealthBarUpdaterGroup {
    private val updaters = ConcurrentHashMap<UUID, HealthBarUpdaterImpl>()
    override fun player(): HealthBarPlayer = player
    override fun entity(): HealthBarEntity = entity

    override fun updaters(): Collection<HealthBarUpdater> = updaters.values

    override fun addHealthBar(data: HealthBarCreateEvent) {
        updaters.computeIfAbsent(data.healthBar.uuid()) {
            HealthBarUpdaterImpl(this, data.healthBar.createRenderer(data))
        }.updateTick()
    }
}