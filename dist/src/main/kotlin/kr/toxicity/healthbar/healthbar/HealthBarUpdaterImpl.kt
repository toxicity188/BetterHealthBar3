package kr.toxicity.healthbar.healthbar

import kr.toxicity.healthbar.api.healthbar.HealthBarUpdater
import kr.toxicity.healthbar.api.healthbar.HealthBarUpdaterGroup
import kr.toxicity.healthbar.api.renderer.HealthBarRenderer

class HealthBarUpdaterImpl(
    private val parent: HealthBarUpdaterGroupImpl,
    private val renderer: HealthBarRenderer
): HealthBarUpdater {

    override fun parent(): HealthBarUpdaterGroup = parent
    override fun renderer(): HealthBarRenderer = renderer

    override fun updateTick() {
        renderer.updateTick()
    }

    override fun remove() {
        renderer.stop()
    }

    override fun update(): Boolean = renderer.work()
}