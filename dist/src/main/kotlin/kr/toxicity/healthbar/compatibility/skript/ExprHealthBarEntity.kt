package kr.toxicity.healthbar.compatibility.skript

import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import org.bukkit.entity.Entity
import org.bukkit.event.Event

class ExprHealthBarEntity : SimpleExpression<Entity>() {
    override fun toString(p0: Event?, p1: Boolean): String = "healthbar entity"
    override fun init(p0: Array<Expression<*>>, p1: Int, p2: Kleenean, p3: SkriptParser.ParseResult): Boolean {
        return true
    }
    override fun isSingle(): Boolean = true
    override fun getReturnType(): Class<out Entity> = Entity::class.java
    override fun get(p0: Event): Array<Entity> = if (p0 is HealthBarCreateEvent) arrayOf(p0.entity.entity()) else emptyArray()
}