package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.compatibility.MythicMobsCompatibility
import kr.toxicity.healthbar.compatibility.PlaceholderAPICompatibility
import kr.toxicity.healthbar.compatibility.CitizensCompatibility
import kr.toxicity.healthbar.compatibility.SkriptCompatibility
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.PLUGIN
import kr.toxicity.hud.api.BetterHudAPI
import kr.toxicity.hud.api.bukkit.event.CreateShaderEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object CompatibilityManager : BetterHealthBerManager {

    private val compMap = mapOf(
        "MythicMobs" to {
            MythicMobsCompatibility()
        },
        "PlaceholderAPI" to {
            PlaceholderAPICompatibility()
        },
        "Citizens" to {
            CitizensCompatibility()
        },
        "Skript" to {
            SkriptCompatibility()
        }
    )

    override fun start() {
        Bukkit.getPluginManager().run {
            if (isPluginEnabled("BetterHud")) {
                BetterHudAPI.inst().shaderManager.addConstant("DISPLAY_HEIGHT", "8192.0 / 40.0")
                registerEvents(object : Listener {
                    @EventHandler
                    fun shader(e: CreateShaderEvent) {
                        if (!ConfigManagerImpl.useCoreShaders()) return
                        PLUGIN.getResource("rendertype_text.vsh")?.let {
                            InputStreamReader(it, StandardCharsets.UTF_8).buffered().use { reader ->
                                var started = false
                                for (s in reader.readLines()) {
                                    if (s.endsWith("//start")) {
                                        started = true
                                        continue
                                    }
                                    if (!started) continue
                                    if (s.endsWith("//end")) {
                                        break
                                    }
                                    e.lines.add(s)
                                }
                            }
                        }
                    }
                }, PLUGIN)
            }
            compMap.forEach {
                if (isPluginEnabled(it.key)) it.value().accept()
            }
        }
    }

    override fun reload(resource: PackResource) {

    }
}