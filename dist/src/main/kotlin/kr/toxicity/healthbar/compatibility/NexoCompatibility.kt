package kr.toxicity.healthbar.compatibility

import com.nexomc.nexo.api.events.resourcepack.NexoPrePackGenerateEvent
import kr.toxicity.healthbar.api.plugin.ReloadState.Failure
import kr.toxicity.healthbar.api.plugin.ReloadState.OnReload
import kr.toxicity.healthbar.api.plugin.ReloadState.Success
import kr.toxicity.healthbar.manager.CompatibilityManager
import kr.toxicity.healthbar.util.PLUGIN
import kr.toxicity.healthbar.util.info
import kr.toxicity.healthbar.util.registerListeners
import kr.toxicity.healthbar.util.warn
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class NexoCompatibility : Compatibility {
    override fun accept() {
        CompatibilityManager.usePackTypeNone = true
        registerListeners(object : Listener {
            @EventHandler
            fun NexoPrePackGenerateEvent.generate() {
                when (val reload = PLUGIN.reload()) {
                    is Success -> {
                        reload.resourcePack.forEach { (key, value) ->
                            addUnknownFile(key, value)
                        }
                        info("Successfully merged with Nexo: (${reload.time} ms)")
                    }
                    is Failure -> {
                        warn(
                            "Resource pack merge failed",
                            "Reason: ${reload.throwable.message}",
                            "Stack trace:",
                            reload.throwable.stackTraceToString()
                        )
                    }
                    is OnReload -> warn(
                        "Resource pack merge failed",
                        "Reason: BetterHealthBar is still on reload."
                    )
                }
            }
        })
        info(
            "BetterHealthBar hooks Nexo.",
            "Be sure to use '/nexo reload all' instead of '/healthbar' to generate resource pack."
        )
    }
}