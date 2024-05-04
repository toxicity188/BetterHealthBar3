package kr.toxicity.healthbar

import kr.toxicity.healthbar.api.BetterHealthBar
import kr.toxicity.healthbar.api.bedrock.BedrockAdapter
import kr.toxicity.healthbar.api.manager.*
import kr.toxicity.healthbar.api.modelengine.ModelEngineAdapter
import kr.toxicity.healthbar.api.nms.NMS
import kr.toxicity.healthbar.api.plugin.ReloadResult
import kr.toxicity.healthbar.api.plugin.ReloadState
import kr.toxicity.healthbar.api.scheduler.WrappedScheduler
import kr.toxicity.healthbar.bedrock.FloodgateAdapter
import kr.toxicity.healthbar.bedrock.GeyserAdapter
import kr.toxicity.healthbar.manager.*
import kr.toxicity.healthbar.modelengine.CurrentModelEngineAdapter
import kr.toxicity.healthbar.modelengine.LegacyModelEngineAdapter
import kr.toxicity.healthbar.pack.PackGenerator
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.scheduler.FoliaScheduler
import kr.toxicity.healthbar.scheduler.StandardScheduler
import kr.toxicity.healthbar.util.*
import kr.toxicity.healthbar.version.MinecraftVersion
import kr.toxicity.healthbar.version.ModelEngineVersion
import org.bukkit.Bukkit
import java.io.File
import java.io.InputStream
import java.text.DecimalFormat
import java.util.function.BiConsumer
import java.util.jar.JarFile

@Suppress("UNUSED")
class BetterHealthBarImpl: BetterHealthBar() {

    private val isFolia = runCatching {
        Class.forName("io.papermc.paper.threadedregions.scheduler.FoliaAsyncScheduler")
        true
    }.getOrDefault(false)
    private val isPaper = isFolia || runCatching {
        Class.forName("com.destroystokyo.paper.profile.PlayerProfile")
        true
    }.getOrDefault(false)
    private var bedrock = BedrockAdapter.NONE
    private var modelEngine = ModelEngineAdapter.NONE
    private lateinit var nms: NMS
    private val scheduler = if (isFolia) FoliaScheduler() else StandardScheduler()

    private val managers = listOf(
        ConfigManagerImpl,
        ListenerManagerImpl,
        PlaceholderManagerImpl,
        ImageManagerImpl,
        TextManagerImpl,
        PlayerManagerImpl,
        LayoutManagerImpl,
        HealthBarManagerImpl
    )

    @Volatile
    private var onReload = false

    override fun onEnable() {
        val log = ArrayList<String>()
        val manager = Bukkit.getPluginManager()
        manager.getPlugin("ModelEngine")?.let {
            runWithHandleException("Failed to load ModelEngine support.") {
                val version = ModelEngineVersion(it.description.version)
                modelEngine = if (version >= ModelEngineVersion.version_4_0_0) CurrentModelEngineAdapter() else LegacyModelEngineAdapter()
                log.add("ModelEngine support enabled. $version")
            }
        }
        nms = when (MinecraftVersion.current) {
            MinecraftVersion.version1_20_5, MinecraftVersion.version1_20_6 -> kr.toxicity.healthbar.nms.v1_20_R4.NMSImpl()
            MinecraftVersion.version1_20_3, MinecraftVersion.version1_20_4 -> kr.toxicity.healthbar.nms.v1_20_R3.NMSImpl()
            else -> {
                warn(
                    "Unsupported version found: ${MinecraftVersion.current}",
                    "Plugin will be disabled."
                )
                manager.disablePlugin(this)
                return
            }
        }
        if (manager.isPluginEnabled("Geyser-Spigot")) {
            log.add("Geyser support enabled.")
            bedrock = GeyserAdapter()
        } else if (manager.isPluginEnabled("floodgate")) {
            log.add("Floodgate support enabled.")
            bedrock = FloodgateAdapter()
        }
        getCommand("healthbar")?.setExecutor { commandSender, _, _, _ ->
            if (commandSender.hasPermission("betterhealthbar.reload")) {
                commandSender.sendMessage("Starts reloading. please wait...")
                asyncTask {
                    val reload = reload()
                    when (reload.state) {
                        ReloadState.SUCCESS -> commandSender.sendMessage("Reload success! (${DecimalFormat.getInstance().format(reload.time)} ms)")
                        ReloadState.FAIL -> commandSender.sendMessage("Failed to reload.")
                        ReloadState.ON_RELOAD -> commandSender.sendMessage("This plugin is still on reload.")
                    }
                }
            } else {
                commandSender.sendMessage("You have no permission.")
            }
            true
        }
        runWithHandleException("Error has occurred while enabling.") {
            managers.forEach {
                it.start()
            }
            log.add("Plugin enabled.")
            asyncTask {
                when (reload().state) {
                    ReloadState.SUCCESS -> info(*log.toTypedArray())
                    else -> {
                        manager.disablePlugin(this)
                    }
                }
            }
        }
    }

    override fun reload(): ReloadResult {
        if (onReload) return ReloadResult(ReloadState.ON_RELOAD, 0)
        val time = System.currentTimeMillis()
        return runWithHandleException("Error has occurred while reloading.") {
            managers.forEach {
                it.preReload()
            }
            val resource = PackResource()
            managers.forEach {
                info("Reloading ${it.javaClass.simpleName}...")
                it.reload(resource)
            }
            PackGenerator.zip(ConfigManagerImpl.packType(), resource)
            managers.forEach {
                it.postReload()
            }
            ReloadResult(ReloadState.SUCCESS, System.currentTimeMillis() - time)
        }.getOrElse {
            ReloadResult(ReloadState.FAIL, System.currentTimeMillis() - time)
        }
    }

    override fun onReload(): Boolean = onReload
    override fun bedrock(): BedrockAdapter = bedrock
    override fun modelEngine(): ModelEngineAdapter = modelEngine
    override fun scheduler(): WrappedScheduler = scheduler
    override fun nms(): NMS = nms
    override fun isFolia(): Boolean = isFolia
    override fun isPaper(): Boolean = isPaper
    override fun loadAssets(prefix: String, dir: File) {
        loadAssets(prefix) { s, i ->
            File(dir, s).apply {
                parentFile.mkdirs()
            }.outputStream().buffered().use { os ->
                i.copyTo(os)
            }
        }
    }
    override fun loadAssets(prefix: String, consumer: BiConsumer<String, InputStream>) {
        JarFile(file).use {
            it.entries().toList().forEachAsync { entry ->
                if (!entry.name.startsWith(prefix)) return@forEachAsync
                if (entry.name.length <= prefix.length + 1) return@forEachAsync
                val name = entry.name.substring(prefix.length + 1)
                if (!entry.isDirectory) {
                    getResource(entry.name)?.buffered()?.use { stream ->
                        consumer.accept(name, stream)
                    }
                }
            }
        }
    }

    override fun configManager(): ConfigManager = ConfigManagerImpl
    override fun imageManager(): ImageManager = ImageManagerImpl
    override fun playerManager(): PlayerManager = PlayerManagerImpl
    override fun layoutManager(): LayoutManager = LayoutManagerImpl
    override fun listenerManager(): ListenerManager = ListenerManagerImpl
    override fun healthBarManager(): HealthBarManager = HealthBarManagerImpl
    override fun textManager(): TextManager = TextManagerImpl
    override fun placeholderManager(): PlaceholderManager = PlaceholderManagerImpl

    override fun onDisable() {
        runWithHandleException("Error has occurred while disabling.") {
            managers.forEach {
                it.end()
            }
            info("Plugin disabled.")
        }
    }
}