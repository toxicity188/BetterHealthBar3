package kr.toxicity.healthbar.equation

import kr.toxicity.healthbar.api.equation.HealthBarEquation
import kr.toxicity.healthbar.util.getAsEquation
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.Vector

class HealthBarEquationImpl(
    private val x: TEquation,
    private val y: TEquation,
    private val z: TEquation
) : HealthBarEquation {
    companion object {
        val zero = HealthBarEquationImpl(
            TEquation.zero,
            TEquation.zero,
            TEquation.zero,
        )
    }

    constructor(section: ConfigurationSection): this (
        section.getAsEquation("x"),
        section.getAsEquation("y"),
        section.getAsEquation("z")
    )

    override fun evaluate(t: Double): Vector = Vector(
        x.evaluate(t),
        y.evaluate(t),
        z.evaluate(t)
    )
}