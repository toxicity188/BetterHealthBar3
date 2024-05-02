package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.api.component.PixelComponent
import kr.toxicity.healthbar.api.component.WidthComponent
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component

const val ADVENTURE_START_INT = 0xA0000

val SPACE_KEY
    get() = Key.key(NAMESPACE, "space")

val NEGATIVE_ONE_SPACE_COMPONENT
    get() = WidthComponent(0, Component.text().font(SPACE_KEY).content((ADVENTURE_START_INT - 1).parseChar()))

val EMPTY_WIDTH_COMPONENT
    get() = WidthComponent(0 , Component.text())
val EMPTY_PIXEL_COMPONENT
    get() = PixelComponent(0, EMPTY_WIDTH_COMPONENT)

fun Int.toAscent() = this - 8192

fun Int.toSpaceComponent() = WidthComponent(this, Component.text().font(SPACE_KEY).content((ADVENTURE_START_INT + this).parseChar()))

fun Int.parseChar(): String {
    return if (this <= 0xFFFF) this.toChar().toString()
    else {
        val t = this - 0x10000
        return "${((t ushr 10) + 0xD800).toChar()}${((t and 1023) + 0xDC00).toChar()}"
    }
}