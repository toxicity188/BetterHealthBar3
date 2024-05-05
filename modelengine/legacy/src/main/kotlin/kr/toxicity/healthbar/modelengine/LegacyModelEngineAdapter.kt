package kr.toxicity.healthbar.modelengine

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.generator.model.BlueprintBone
import kr.toxicity.healthbar.api.modelengine.ModelEngineAdapter
import org.bukkit.entity.Entity

class LegacyModelEngineAdapter: ModelEngineAdapter {
    override fun height(entity: Entity): Double? {
        return ModelEngineAPI.getModeledEntity(entity.uniqueId)?.run {
            models.values.maxOf {
                fun getChildren(blueprint: BlueprintBone): Double {
                    val children: Set<BlueprintBone>? = blueprint.children
                    return if (children.isNullOrEmpty()) blueprint.globalOrigin.y
                    else children.maxOf { bb ->
                        getChildren(bb)
                    }
                }
                it.blueprint.bones.values.maxOf { bb ->
                    getChildren(bb)
                }
            }
        }
    }
}