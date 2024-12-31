package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.api.component.PixelComponent
import kr.toxicity.healthbar.api.component.WidthComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

const val NEW_LAYER_INT = 0xA0000
const val ADVENTURE_START_INT = 0xB0000

val LEGACY = LegacyComponentSerializer.builder()
    .useUnusualXRepeatedCharacterHexFormat()
    .build()

val MINI_MESSAGE = MiniMessage.builder()
    .tags(
        TagResolver.resolver(
        StandardTags.decorations(),
        StandardTags.color(),
        StandardTags.gradient(),
        StandardTags.rainbow(),
        StandardTags.translatable(),
        StandardTags.translatableFallback(),
        StandardTags.transition(),
        StandardTags.insertion(),
        StandardTags.selector(),
        StandardTags.score(),
        StandardTags.nbt(),
    )).postProcessor {
        val style = it.style()
        it.style(style.decorations(TextDecoration.entries.associateWith { d ->
            val deco = style.decoration(d)
            if (deco == TextDecoration.State.NOT_SET) TextDecoration.State.FALSE else TextDecoration.State.TRUE
        }))
    }
    .build()

val SPACE_KEY
    get() = createAdventureKey("space")

val NEGATIVE_ONE_SPACE_COMPONENT
    get() = WidthComponent(0, Component.text().font(SPACE_KEY).content((ADVENTURE_START_INT - 1).parseChar()))
val NEW_LAYER
    get() = WidthComponent(0, Component.text().font(SPACE_KEY).content(NEW_LAYER_INT.parseChar()))

val EMPTY_WIDTH_COMPONENT
    get() = WidthComponent(0 , Component.text())
val EMPTY_PIXEL_COMPONENT
    get() = PixelComponent(0, EMPTY_WIDTH_COMPONENT)

fun Int.toAscent() = this - 8192
fun Int.toHeight() = apply {
    if (this > 256) throw RuntimeException("Too large height: $this > 256")
    if (this < 0) throw RuntimeException("Too low height: $this < 0")
}

fun Int.toSpaceComponent() = WidthComponent(this, Component.text().font(SPACE_KEY).content((ADVENTURE_START_INT + this).parseChar()))

fun Int.parseChar(): String {
    return if (this <= 0xFFFF) this.toChar().toString()
    else {
        val t = this - 0x10000
        return "${((t ushr 10) + 0xD800).toChar()}${((t and 1023) + 0xDC00).toChar()}"
    }
}

fun WidthComponent.toPixelComponent(int: Int) = PixelComponent(int, this)