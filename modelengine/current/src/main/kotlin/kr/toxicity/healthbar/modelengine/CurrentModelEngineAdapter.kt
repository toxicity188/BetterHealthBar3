package kr.toxicity.healthbar.modelengine

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone
import kr.toxicity.healthbar.api.modelengine.ModelAdapter
import net.jodah.expiringmap.ExpirationPolicy
import net.jodah.expiringmap.ExpiringMap
import org.bukkit.entity.Entity
import java.util.concurrent.TimeUnit

class CurrentModelEngineAdapter : ModelAdapter {

    private val blueprintCache = ExpiringMap.builder()
        .maxSize(256)
        .expirationPolicy(ExpirationPolicy.ACCESSED)
        .expiration(1, TimeUnit.MINUTES)
        .build<String, Double>()

    override fun height(entity: Entity): Double? {
        return ModelEngineAPI.getModeledEntity(entity.uniqueId)?.run {
            models.values.maxOfOrNull {
                blueprintCache.computeIfAbsent(it.blueprint.name) { _ ->
                    fun getChildren(blueprint: BlueprintBone): Double {
                        val children: Collection<BlueprintBone> = blueprint.children.values
                        return if (children.isEmpty()) blueprint.globalPosition.y.toDouble()
                        else children.maxOf { bb ->
                            getChildren(bb)
                        }
                    }
                    it.blueprint.bones.values.maxOfOrNull { bb ->
                        getChildren(bb)
                    } ?: 0.0
                }
            }
        }
    }
}