package kr.toxicity.healthbar.nms.v1_20_R1

import kr.toxicity.library.sharedpackets.PluginBundlePacket
import net.kyori.adventure.key.Key
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBundlePacket

private val KEY = Key.key("betterhealthbar")

fun Iterable<Packet<ClientGamePacketListener>>.toBundlePacket() = ClientboundBundlePacket(PluginBundlePacket.of(
    KEY,
    this
))