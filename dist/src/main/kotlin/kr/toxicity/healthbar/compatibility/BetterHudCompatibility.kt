package kr.toxicity.healthbar.compatibility

import kr.toxicity.healthbar.manager.CompatibilityManager
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.util.PLUGIN
import kr.toxicity.hud.api.BetterHudAPI
import kr.toxicity.hud.api.manager.ShaderManager
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class BetterHudCompatibility : Compatibility {

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

    override fun accept() {
        CompatibilityManager.hookOtherShaders = true
        BetterHudAPI.inst().shaderManager.run {
            addConstant("DISPLAY_HEIGHT", "8192.0 / 40.0")
            addTagSupplier(ShaderManager.ShaderType.TEXT_VERTEX) {
                if (ConfigManagerImpl.useCoreShaders()) {
                    val vsh = loadShaderLine("text.vsh")
                    ShaderManager.newTag()
                        .add("GenerateOtherMainMethod", vsh.range("//GenerateOtherMainMethod"))
                        .add("GenerateOtherDefinedMethod", vsh.range("//GenerateOtherDefinedMethod"))
                } else ShaderManager.newTag()
            }
            addTagSupplier(ShaderManager.ShaderType.TEXT_FRAGMENT) {
                if (ConfigManagerImpl.useCoreShaders()) {
                    val fsh = loadShaderLine("text.fsh")
                    ShaderManager.newTag()
                        .add("GenerateOtherMainMethod", fsh.range("//GenerateOtherMainMethod"))
                        .add("GenerateOtherDefinedMethod", fsh.range("//GenerateOtherDefinedMethod"))
                } else ShaderManager.newTag()
            }
        }
    }
}