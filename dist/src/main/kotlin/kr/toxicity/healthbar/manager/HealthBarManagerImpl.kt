package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.healthbar.HealthBar
import kr.toxicity.healthbar.api.manager.HealthBarManager
import kr.toxicity.healthbar.healthbar.HealthBarImpl
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.forEachAllYaml
import kr.toxicity.healthbar.util.putSync
import kr.toxicity.healthbar.util.runWithHandleException
import kr.toxicity.healthbar.util.subFolder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object HealthBarManagerImpl : HealthBarManager, BetterHealthBerManager {

    private val healthBarMap = ConcurrentHashMap<String, HealthBarImpl>()

    override fun healthBar(name: String): HealthBar? {
        return healthBarMap[name]
    }

    override fun allHealthBars(): Collection<HealthBar> {
        return healthBarMap.values
    }

    override fun reload(resource: PackResource) {
        healthBarMap.clear()
        resource.dataFolder.subFolder("healthbars").forEachAllYaml { file, s, section ->
            runWithHandleException("Unable to load this health bar: $s in ${file.path}") {
                val healthBar = HealthBarImpl(
                    file.path,
                    s,
                    UUID.randomUUID(),
                    section
                )
                healthBarMap.putSync("healthbar", s) {
                    healthBar
                }
            }
        }
    }
}