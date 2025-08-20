package kr.toxicity.healthbar.equation

import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.E
import kotlin.math.PI

class TEquation(
    expression: String
) {
    companion object {
        val zero = TEquation("0")
    }

    private val expr = ExpressionBuilder(expression)
        .variable("t")
        .variable("pi")
        .variable("e")
        .build()

    fun evaluate(t: Double) = Expression(expr)
        .setVariable("t", t)
        .setVariable("pi", PI)
        .setVariable("e", E)
        .evaluate()
}