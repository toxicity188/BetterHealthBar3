package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.compatibility.*
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.PLUGIN
import kr.toxicity.hud.api.BetterHudAPI
import kr.toxicity.hud.api.manager.ShaderManager
import org.bukkit.Bukkit
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
        },
        "Parties" to {
            PartiesCompatibility()
        },
        "LevelledMobs" to {
            LevelledMobsCompatibility()
        }
    )

    private fun List<String>.range(key: String): List<String> {
        val list = ArrayList<String>()
        var add = false
        forEach {
            if (it.trim() == key) {
                if (!add) add = true
                else return list
            } else if (add) {
                list.add(it)
            }
        }
        return list
    }
    private fun loadShaderLine(name: String) = PLUGIN.getResource(name)?.let {
        InputStreamReader(it, StandardCharsets.UTF_8).buffered().use { reader ->
            reader.readLines()
        }
    } ?: emptyList()

    override fun start() {
        Bukkit.getPluginManager().run {
            if (isPluginEnabled("BetterHud")) {
                BetterHudAPI.inst().shaderManager.run {
                    addConstant("DISPLAY_HEIGHT", "8192.0 / 40.0")
                    addTagSupplier(ShaderManager.ShaderType.TEXT_VERTEX) {
                        if (ConfigManagerImpl.useCoreShaders()) {
                            val vsh = loadShaderLine("rendertype_text.vsh")
                            ShaderManager.newTag()
                                .add("GenerateOtherMainMethod", vsh.range("//GenerateOtherMainMethod"))
                        } else ShaderManager.newTag()
                    }
                    addTagSupplier(ShaderManager.ShaderType.TEXT_FRAGMENT) {
                        if (ConfigManagerImpl.useCoreShaders()) {
                            val fsh = loadShaderLine("rendertype_text.fsh")
                            ShaderManager.newTag()
                                .add("GenerateOtherMainMethod", fsh.range("//GenerateOtherMainMethod"))
                                .add("GenerateOtherDefinedMethod", fsh.range("//GenerateOtherDefinedMethod"))
                        } else ShaderManager.newTag()
                    }
                }
            }
            compMap.forEach {
                if (isPluginEnabled(it.key)) it.value().accept()
            }
        }
    }

    override fun reload(resource: PackResource) {

    }
}