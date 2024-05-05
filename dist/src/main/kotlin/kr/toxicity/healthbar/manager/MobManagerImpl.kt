package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.entity.HealthBarEntity
import kr.toxicity.healthbar.api.manager.MobManager
import kr.toxicity.healthbar.api.mob.HealthBarMob
import kr.toxicity.healthbar.api.mob.MobConfiguration
import kr.toxicity.healthbar.api.mob.MobProvider
import kr.toxicity.healthbar.compatibility.MythicMobsMobProvider
import kr.toxicity.healthbar.entity.HealthBarEntityImpl
import kr.toxicity.healthbar.mob.MobConfigurationImpl
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.forEachAllYamlAsync
import kr.toxicity.healthbar.util.putSync
import kr.toxicity.healthbar.util.runWithHandleException
import kr.toxicity.healthbar.util.subFolder
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import java.util.concurrent.ConcurrentHashMap

object MobManagerImpl: BetterHealthBerManager, MobManager {
    private val mobProviders = ArrayList<MobProvider>()

    private val mobConfigurationMap = ConcurrentHashMap<String, MobConfiguration>()

    override fun start() {
        Bukkit.getPluginManager().run {
            if (isPluginEnabled("MythicMobs")) mobProviders.add(MythicMobsMobProvider())
        }
    }

    override fun reload(resource: PackResource) {
        mobConfigurationMap.clear()
        resource.dataFolder.subFolder("mobs").forEachAllYamlAsync { file, s, configurationSection ->
            runWithHandleException("Unable to load mob: $s in ${file.path}") {
                val config = MobConfigurationImpl(file.path, configurationSection)
                mobConfigurationMap.putSync("mob", s) {
                    config
                }
            }
        }
    }

    override fun configuration(name: String): MobConfiguration? = mobConfigurationMap[name]
    override fun entity(livingEntity: LivingEntity): HealthBarEntity = HealthBarEntityImpl(livingEntity)

    override fun provide(entity: LivingEntity): HealthBarMob? = mobProviders.firstNotNullOfOrNull {
        it.provide(entity)
    }
}