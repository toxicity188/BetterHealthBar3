package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.layout.LayoutGroup
import kr.toxicity.healthbar.api.manager.LayoutManager
import kr.toxicity.healthbar.layout.LayoutGroupImpl
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.forEachAllYamlAsync
import kr.toxicity.healthbar.util.putSync
import kr.toxicity.healthbar.util.runWithHandleException
import kr.toxicity.healthbar.util.subFolder
import java.util.concurrent.ConcurrentHashMap

object LayoutManagerImpl: LayoutManager, BetterHealthBerManager {

    private val layoutMap = ConcurrentHashMap<String, LayoutGroupImpl>()

    override fun group(name: String): LayoutGroup? {
        return layoutMap[name]
    }

    override fun reload(resource: PackResource) {
        layoutMap.clear()
        resource.dataFolder.subFolder("layouts").forEachAllYamlAsync { file, s, configurationSection ->
            runWithHandleException("Unable to load this layout: $s in ${file.path}") {
                val layout = LayoutGroupImpl(
                    file.path,
                    s,
                    resource,
                    configurationSection
                )
                layoutMap.putSync("layout", s) {
                    layout
                }
            }
        }
    }
}