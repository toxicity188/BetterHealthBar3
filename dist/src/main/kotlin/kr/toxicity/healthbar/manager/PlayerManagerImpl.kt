package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.manager.PlayerManager
import kr.toxicity.healthbar.api.player.HealthBarPlayer
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.pack.PackUploader
import kr.toxicity.healthbar.player.HealthBarPlayerImpl
import kr.toxicity.healthbar.util.PLUGIN
import kr.toxicity.healthbar.util.ifNull
import kr.toxicity.healthbar.util.registerListeners
import kr.toxicity.healthbar.util.taskLater
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object PlayerManagerImpl : PlayerManager, BetterHealthBerManager {
    private val playerMap = ConcurrentHashMap<UUID, HealthBarPlayer>()

    override fun preReload() {
        playerMap.values.forEach {
            it.clear()
        }
    }

    override fun start() {
        registerListeners(object : org.bukkit.event.Listener {
            @EventHandler
            fun join(e: PlayerJoinEvent) {
                val player = e.player
                if (PLUGIN.bedrock().isBedrockPlayer(player.uniqueId)) return
                playerMap[player.uniqueId] = HealthBarPlayerImpl(PLUGIN.nms().foliaAdapt(player))
                taskLater(20) {
                    PackUploader.apply(player)
                }
            }
            @EventHandler
            fun remove(e: PlayerQuitEvent) {
                playerMap.remove(e.player.uniqueId)?.uninject()
            }
            @EventHandler
            fun change(e: PlayerChangedWorldEvent) {
                playerMap[e.player.uniqueId]?.clear()
            }
        })
    }

    override fun reload(resource: PackResource) {
    }

    override fun player(player: Player): HealthBarPlayer {
        return playerMap[player.uniqueId].ifNull("This player is not online.")
    }
    override fun player(uuid: UUID): HealthBarPlayer? {
        return playerMap[uuid]
    }
}