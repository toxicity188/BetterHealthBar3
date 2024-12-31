package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.layout.LayoutGroup
import kr.toxicity.healthbar.api.manager.LayoutManager
import kr.toxicity.healthbar.layout.LayoutGroupImpl
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.forEachAllYaml
import kr.toxicity.healthbar.util.putSync
import kr.toxicity.healthbar.util.runWithHandleException
import kr.toxicity.healthbar.util.subFolder
import java.util.Collections
import java.util.HashSet
import java.util.concurrent.ConcurrentHashMap

object LayoutManagerImpl : LayoutManager, BetterHealthBerManager {

    private val layoutMap = ConcurrentHashMap<String, LayoutGroupImpl>()
    private val groupData = ConcurrentHashMap<String, MutableSet<LayoutGroupImpl>>()

    override fun name(name: String): LayoutGroup? {
        return layoutMap[name]
    }

    override fun group(group: String): List<LayoutGroup> {
        return groupData[group]?.toList() ?: emptyList()
    }

    override fun reload(resource: PackResource) {
        layoutMap.clear()
        groupData.clear()

        resource.dataFolder.subFolder("layouts").forEachAllYaml { file, s, configurationSection ->
            runWithHandleException("Unable to load this layout: $s in ${file.path}") {
                val layout = LayoutGroupImpl(
                    file.path,
                    s,
                    configurationSection
                )
                layoutMap.putSync("layout", s) {
                    layout
                }
            }
        }
        layoutMap.forEach {
            it.value.group()?.let { group ->
                groupData.computeIfAbsent(group) {
                    Collections.synchronizedSet(HashSet())
                }.add(it.value)
            } ?: it.value.build(resource, 1)
        }
        groupData.values.forEach {
            it.forEach { group ->
                group.build(resource, it.size)
            }
        }
    }
}