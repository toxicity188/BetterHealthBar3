package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.BetterHealthBar
import kr.toxicity.healthbar.api.configuration.CoreShadersOption
import kr.toxicity.healthbar.api.manager.ConfigManager
import kr.toxicity.healthbar.api.pack.PackType
import kr.toxicity.healthbar.configuration.PluginConfiguration
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.*
import org.bstats.bukkit.Metrics
import org.bukkit.entity.EntityType
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Collections
import java.util.EnumSet

object ConfigManagerImpl : ConfigManager, BetterHealthBerManager {

    private var debug = true
    private var metrics = true
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
    private var numberFormat = DecimalFormat.getNumberInstance()
    private var selfHostPort = 8163
    private var blackListEntityType = emptySet<EntityType>()
    private var disableToInvulnerableMob = true
    private var disableToInvisibleMob = true
    private var shaders = CoreShadersOption.DEFAULT
    private var useCoreShaders = true
    private var showMeHealthBar = true
    private var resourcePackObfuscation = false

    private var bstats: Metrics? = null

    override fun start() {
        preReload()
    }

    override fun preReload() {
        runWithHandleException("Unable to load config.yml") {
            val config = PluginConfiguration.CONFIG.create()
            debug = config.getBoolean("debug")
            resourcePackObfuscation = config.getBoolean("resource-pack-obfuscation", false)

            packType = config.getString("pack-type")?.let {
                runCatching {
                    PackType.valueOf(it.uppercase())
                }.getOrElse {
                    warn("Unable to find this pack: $it")
                    PackType.FOLDER
                }
            } ?: PackType.FOLDER
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
            numberFormat = runCatching {
                DecimalFormat(config.getString("number-format", "#,###"))
            }.getOrElse {
                DecimalFormat.getNumberInstance()
            }
            blackListEntityType = Collections.unmodifiableSet(EnumSet.copyOf(config.getStringList("blacklist-entity-type").mapNotNull {
                runCatching {
                    EntityType.valueOf(it.uppercase())
                }.getOrNull()
            }))
            disableToInvulnerableMob = config.getBoolean("disable-to-invulnerable-mob", true)
            disableToInvisibleMob = config.getBoolean("disable-to-invisible-mob", true)
            config.getConfigurationSection("shaders")?.let { s ->
                shaders = CoreShadersOption(
                    s.getBoolean("text.vsh", true),
                    s.getBoolean("text.fsh", true),
                    s.getBoolean("text.json", true)
                )
            }
            useCoreShaders = config.getBoolean("use-core-shaders", true)
            showMeHealthBar = config.getBoolean("show-me-healthbar", true)

            if (!metrics) {
                bstats?.shutdown()
                bstats = null
            } else if (bstats == null) {
                bstats = Metrics(PLUGIN, 21802)
            }
        }
    }

    override fun reload(resource: PackResource) {
    }

    override fun debug(): Boolean = debug
    override fun metrics(): Boolean = metrics
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
    override fun numberFormat(): NumberFormat = numberFormat
    override fun blacklistEntityType(): Set<EntityType> = blackListEntityType
    override fun disableToInvulnerableMob(): Boolean = disableToInvulnerableMob
    override fun disableToInvisibleMob(): Boolean = disableToInvisibleMob
    override fun shaders(): CoreShadersOption = shaders
    override fun useCoreShaders(): Boolean = useCoreShaders
    override fun showMeHealthBar(): Boolean = showMeHealthBar
    override fun resourcePackObfuscation(): Boolean = resourcePackObfuscation
}