package kr.toxicity.healthbar.compatibility

import io.th0rgal.oraxen.api.events.OraxenPackGeneratedEvent
import io.th0rgal.oraxen.utils.VirtualFile
import kr.toxicity.healthbar.api.pack.PackType
import kr.toxicity.healthbar.api.plugin.ReloadState.*
import kr.toxicity.healthbar.manager.CompatibilityManager
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.util.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.io.ByteArrayInputStream

class OraxenCompatibility : Compatibility {
    override fun accept() {
        CompatibilityManager.usePackTypeNone = true
        registerListeners(object : Listener {
            @EventHandler
            fun OraxenPackGeneratedEvent.generate() {
                ConfigManagerImpl.preReload()
                if (ConfigManagerImpl.packType() == PackType.NONE) when (val reload = PLUGIN.reload()) {
                    is Success -> {
                        reload.resourcePack.forEach { (key, value) ->
                            output.add(
                                VirtualFile(
                                    key.substringBeforeLast('/'),
                                    key.substringAfterLast('/'),
                                    ByteArrayInputStream(value).buffered()
                                )
                            )
                        }
                        info("Successfully merged with Oraxen: (${reload.time} ms)")
                    }
                    is Failure -> {
                        reload.throwable.handleException("Resource pack merge failed.")
                    }
                    is OnReload -> warn(
                        "Resource pack merge failed",
                        "Reason: BetterHealthBar is still on reload."
                    )
                }
            }
        })
        info(
            "BetterHealthBar hooks Oraxen.",
            "Be sure to use '/oraxen reload all' instead of '/healthbar' to generate resource pack."
        )
    }
}