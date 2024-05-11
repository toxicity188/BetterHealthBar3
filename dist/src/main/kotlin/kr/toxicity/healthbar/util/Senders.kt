package kr.toxicity.healthbar.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.CommandSender

val PREFIX = Component.text()
    .content("[$NAMESPACE]")
    .color(NamedTextColor.AQUA)
    .build()

private val EMPTY_DECORATION = TextDecoration.entries.associateWith {
    TextDecoration.State.FALSE
}

val CommandSender.adventure
    get() = PLUGIN.audiences().sender(this)

fun CommandSender.info(component: Component) = adventure.sendMessage(
    Component.text()
        .decorations(EMPTY_DECORATION)
        .append(PREFIX)
        .append(component)
        .build()
)