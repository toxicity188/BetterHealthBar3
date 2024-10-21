package kr.toxicity.healthbar.compatibility

import ch.njol.skript.Skript
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.VariableString
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.compatibility.skript.ExprHealthBarEntity
import kr.toxicity.healthbar.compatibility.skript.ExprHealthBarPlayer
import kr.toxicity.healthbar.util.ifNull
import kr.toxicity.healthbar.util.placeholder
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.function.Function

class SkriptCompatibility : Compatibility {
    override fun accept() {
        Skript.registerExpression(ExprHealthBarPlayer::class.java, Player::class.java, ExpressionType.SIMPLE, "healthbar player")
        Skript.registerExpression(ExprHealthBarEntity::class.java, Entity::class.java, ExpressionType.SIMPLE, "healthbar entity")

        PlaceholderContainer.STRING.addPlaceholder("skript_variable", placeholder(1) { args ->
            val value = VariableString.newInstance(args.joinToString(",")).ifNull("Invalid variable.")
            Function {
                value.getSingle(it) ?: "<none>"
            }
        })
    }
}