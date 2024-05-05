package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.PLUGIN
import kr.toxicity.hud.api.BetterHud
import kr.toxicity.hud.api.event.CreateShaderEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object CompatibilityManager: BetterHealthBerManager {
    override fun start() {
        Bukkit.getPluginManager().run {
            if (isPluginEnabled("BetterHud")) {
                BetterHud.getInstance().shaderManager.addConstant("DISPLAY_HEIGHT", "8192.0 / 40.0")
                registerEvents(object : Listener {
                    @EventHandler
                    fun shader(e: CreateShaderEvent) {
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
        }
    }

    override fun reload(resource: PackResource) {

    }
}