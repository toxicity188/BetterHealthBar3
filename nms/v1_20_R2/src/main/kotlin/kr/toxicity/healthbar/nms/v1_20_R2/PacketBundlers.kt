package kr.toxicity.healthbar.nms.v1_20_R2

import kr.toxicity.healthbar.api.nms.PacketBundler
import kr.toxicity.library.sharedpackets.PluginBundlePacket
import net.kyori.adventure.key.Key
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBundlePacket
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer
import org.bukkit.entity.Player

internal fun bundlerOf(vararg packets: ClientPacket) = BundlerImpl(if (packets.isEmpty()) arrayListOf() else packets.toMutableList())
internal val KEY = Key.key("betterhealthbar")
internal typealias ClientPacket = Packet<ClientGamePacketListener>
internal operator fun PacketBundler.plusAssign(other: ClientPacket) {
    when (this) {
        is BundlerImpl -> add(other)
        else -> throw RuntimeException("unsupported bundler.")
    }
}

internal class BundlerImpl(
    private val list: MutableList<ClientPacket>
) : PacketBundler, PluginBundlePacket<ClientPacket> by PluginBundlePacket.of(KEY, list) {
    val bundlePacket = ClientboundBundlePacket(this)
    override fun send(player: Player) {
        if (list.isEmpty()) return
        val connection = (player as CraftPlayer).handle.connection
        connection.send(bundlePacket)
    }
    fun add(other: ClientPacket) {
        list += other
    }
}