package kr.toxicity.healthbar.compatibility

import com.alessiodp.parties.bukkit.BukkitPartiesPlugin
import kr.toxicity.healthbar.api.placeholder.PlaceholderContainer
import kr.toxicity.healthbar.util.placeholder
import org.bukkit.Bukkit
import java.util.function.Function

class PartiesCompatibility : Compatibility {
    override fun accept() {
        val parties = Class.forName("com.alessiodp.parties.core.bukkit.bootstrap.ADPBukkitBootstrap").getDeclaredField("plugin").run {
            isAccessible = true
            get(Bukkit.getPluginManager().getPlugin("Parties"))
        } as BukkitPartiesPlugin
        PlaceholderContainer.BOOL.addPlaceholder("parties_is_leader", placeholder(0) {
            Function get@ {
                val uuid = it.entity.entity().uniqueId
                val player = parties.playerManager.getPlayer(uuid) ?: return@get false
                val party = parties.partyManager.getPartyOfPlayer(player) ?: return@get false
                party.leader == player.playerUUID
            }
        })
        PlaceholderContainer.BOOL.addPlaceholder("parties_is_member", placeholder(0) {
            Function get@{
                val my = parties.playerManager.getPlayer(it.player.player().uniqueId)?.partyId ?: return@get false
                val other = parties.playerManager.getPlayer(it.entity.entity().uniqueId)?.partyId ?: return@get false
                my == other
            }
        })
    }
}