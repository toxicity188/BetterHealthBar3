package kr.toxicity.healthbar.modelengine

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone
import kr.toxicity.healthbar.api.modelengine.ModelEngineAdapter
import org.bukkit.entity.Entity

class CurrentModelEngineAdapter: ModelEngineAdapter {
    override fun getHeight(entity: Entity): Double {
        return ModelEngineAPI.getModeledEntity(entity.uniqueId)?.run {
            models.values.maxOf {
                fun getChildren(blueprint: BlueprintBone): Double {
                    val children: Collection<BlueprintBone> = blueprint.children.values
                    return if (children.isEmpty()) blueprint.globalPosition.y.toDouble()
                    else children.maxOf { bb ->
                        getChildren(bb)
                    }
                }
                it.blueprint.bones.values.maxOf { bb ->
                    getChildren(bb)
                }
            } - 0.75
        } ?: 0.0
    }
}