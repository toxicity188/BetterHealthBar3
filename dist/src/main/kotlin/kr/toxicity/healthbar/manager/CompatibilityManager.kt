package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.compatibility.*
import kr.toxicity.healthbar.pack.PackResource
import org.bukkit.Bukkit

object CompatibilityManager : BetterHealthBerManager {

    var hookOtherShaders = false
    var usePackTypeNone = false

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
        },
        "Parties" to {
            PartiesCompatibility()
        },
        "LevelledMobs" to {
            LevelledMobsCompatibility()
        },
        "BetterHud" to {
            BetterHudCompatibility()
        },
        "Nexo" to {
            NexoCompatibility()
        },
        "Oraxen" to {
            OraxenCompatibility()
        }
    )

    override fun start() {
        Bukkit.getPluginManager().run {
            compMap.forEach {
                if (isPluginEnabled(it.key)) it.value().accept()
            }
        }
    }

    override fun reload(resource: PackResource) {

    }
}