package kr.toxicity.healthbar.compatibility

import ch.njol.skript.Skript
import ch.njol.skript.lang.VariableString
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.compatibility.skript.ExprHealthBarEntity
import kr.toxicity.healthbar.compatibility.skript.ExprHealthBarPlayer
import kr.toxicity.healthbar.util.PLUGIN
import kr.toxicity.healthbar.util.ifNull
import kr.toxicity.healthbar.util.placeholder
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.skriptlang.skript.registration.DefaultSyntaxInfos
import org.skriptlang.skript.registration.SyntaxRegistry
import java.util.function.Function

class SkriptCompatibility : Compatibility {
    override fun accept() {
        Skript.instance().registerAddon(PLUGIN.javaClass, "betterhealthbar").syntaxRegistry().run {
            register(
                SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(ExprHealthBarPlayer::class.java, Player::class.java)
                    .priority(DefaultSyntaxInfos.Expression.SIMPLE)
                    .addPattern("healthbar player")
                    .build()
            )
            register(
                SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(ExprHealthBarEntity::class.java, Entity::class.java)
                    .priority(DefaultSyntaxInfos.Expression.SIMPLE)
                    .addPattern("healthbar entity")
                    .build()
            )
        }

        PlaceholderContainer.STRING.addPlaceholder("skript_variable", placeholder(1) { args ->
            val value = VariableString.newInstance(args.joinToString(",")).ifNull { "Invalid variable." }
            Function {
                value.getSingle(it) ?: "<none>"
            }
        })
    }
}