package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.BetterHealthBar
import kr.toxicity.healthbar.api.manager.ConfigManager
import kr.toxicity.healthbar.api.pack.PackType
import kr.toxicity.healthbar.configuration.PluginConfiguration
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.DATA_FOLDER
import kr.toxicity.healthbar.util.isValidPackNamespace
import kr.toxicity.healthbar.util.runWithHandleException
import kr.toxicity.healthbar.util.warn
import java.io.File

object ConfigManagerImpl: ConfigManager, BetterHealthBerManager {

    private var debug = true
    private var packType = PackType.FOLDER
    private lateinit var buildFolder: File
    private var namespace = BetterHealthBar.NAMESPACE
    private var defaultDuration = 60
    private var defaultHeight = 1.0
    private var lookDegree = 20.0
    private var lookDistance = 20.0
    private var mergeOtherFolder = emptySet<String>()
    private var createPackMemeta = true
    private var enableSelfHost = false
    private var selfHostPort = 8163

    override fun preReload() {
        runWithHandleException("Unable to load config.yml") {
            val config = PluginConfiguration.CONFIG.create()
            debug = config.getBoolean("debug")
            config.getString("pack-type")?.let {
                packType = runCatching {
                    PackType.valueOf(it.uppercase())
                }.getOrElse {
                    warn("Unable to find this pack: $it")
                    PackType.FOLDER
                }
            }
            buildFolder = config.getString("build-folder")?.let {
                File(DATA_FOLDER.parentFile, it.replace('/', File.separatorChar))
            } ?: File(DATA_FOLDER, "build")
            namespace = config.getString("namespace")?.let {
                if (it.isValidPackNamespace()) it else {
                    warn("Invalid namespace: $it")
                    BetterHealthBar.NAMESPACE
                }
            } ?: BetterHealthBar.NAMESPACE
            defaultDuration = config.getInt("default-duration", 60)
            defaultHeight = config.getDouble("default-height", 1.0)
            lookDegree = Math.toRadians(config.getDouble("look-degree", 20.0).coerceAtLeast(1.0))
            lookDistance = config.getDouble("look-distance", 15.0).coerceAtLeast(1.0)
            mergeOtherFolder = config.getStringList("merge-other-folder").map {
                it.replace('/', File.separatorChar)
            }.toSet()
            createPackMemeta = config.getBoolean("create-pack-mcmeta", true)
            enableSelfHost = config.getBoolean("enable-self-host", false)
            selfHostPort = config.getInt("self-host-port", 8163)
        }
    }

    override fun reload(resource: PackResource) {
    }

    override fun debug(): Boolean = debug
    override fun packType(): PackType = packType
    override fun buildFolder(): File = buildFolder
    override fun namespace(): String = namespace
    override fun defaultDuration(): Int = defaultDuration
    override fun defaultHeight(): Double = defaultHeight
    override fun lookDegree(): Double = lookDegree
    override fun lookDistance(): Double = lookDistance
    override fun mergeOtherFolder(): Set<String> = mergeOtherFolder
    override fun createPackMcmeta(): Boolean = createPackMemeta
    override fun enableSelfHost(): Boolean = enableSelfHost
    override fun selfHostPort(): Int = selfHostPort
}