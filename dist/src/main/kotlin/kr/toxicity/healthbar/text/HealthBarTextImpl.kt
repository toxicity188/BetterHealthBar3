package kr.toxicity.healthbar.text

import kr.toxicity.healthbar.api.text.HealthBarText
import kr.toxicity.healthbar.api.text.TextBitmap

class HealthBarTextImpl(
    private val path: String,
    private val charWidth: Map<Int, Int>,
    private val bitmap: List<TextBitmap>,
    private val height: Int,
): HealthBarText {
    override fun path(): String = path
    override fun chatWidth(): Map<Int, Int> = charWidth
    override fun bitmap(): List<TextBitmap> = bitmap
    override fun height(): Int = height
}