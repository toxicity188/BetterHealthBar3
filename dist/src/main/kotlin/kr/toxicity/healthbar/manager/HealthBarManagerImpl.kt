package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.healthbar.HealthBar
import kr.toxicity.healthbar.api.manager.HealthBarManager
import kr.toxicity.healthbar.healthbar.HealthBarImpl
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.forEachAllYaml
import kr.toxicity.healthbar.util.putSync
import kr.toxicity.healthbar.util.runWithHandleException
import kr.toxicity.healthbar.util.subFolder
import java.util.Collections
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object HealthBarManagerImpl : HealthBarManager, BetterHealthBerManager {

    private val healthBarMap = ConcurrentHashMap<String, HealthBarImpl>()
    private val uuidSet = Collections.synchronizedSet(HashSet<UUID>())

    override fun healthBar(name: String): HealthBar? {
        return healthBarMap[name]
    }

    override fun allHealthBars(): Collection<HealthBar> {
        return healthBarMap.values
    }

    override fun reload(resource: PackResource) {
        healthBarMap.clear()
        uuidSet.clear()
        resource.dataFolder.subFolder("healthbars").forEachAllYaml { file, s, section ->
            runWithHandleException("Unable to load this health bar: $s in ${file.path}") {
                var uuid = UUID.randomUUID()
                while (!uuidSet.add(uuid)) uuid = UUID.randomUUID()
                val healthBar = HealthBarImpl(
                    file.path,
                    s,
                    uuid,
                    section
                )
                healthBarMap.putSync("healthbar", s) {
                    healthBar
                }
            }
        }
    }
}