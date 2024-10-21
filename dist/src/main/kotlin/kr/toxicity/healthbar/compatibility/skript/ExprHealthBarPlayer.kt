package kr.toxicity.healthbar.compatibility.skript

import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import org.bukkit.entity.Player
import org.bukkit.event.Event

class ExprHealthBarPlayer : SimpleExpression<Player>() {
    override fun toString(p0: Event?, p1: Boolean): String = "healthbar player"
    override fun init(p0: Array<Expression<*>>, p1: Int, p2: Kleenean, p3: SkriptParser.ParseResult): Boolean {
        return true
    }
    override fun isSingle(): Boolean = true
    override fun getReturnType(): Class<out Player> = Player::class.java
    override fun get(p0: Event): Array<Player> = if (p0 is HealthBarCreateEvent) arrayOf(p0.player.player()) else emptyArray()
}