package kr.toxicity.healthbar

import kr.toxicity.healthbar.api.BetterHealthBar
import kr.toxicity.healthbar.api.bedrock.BedrockAdapter
import kr.toxicity.healthbar.api.manager.*
import kr.toxicity.healthbar.api.modelengine.ModelAdapter
import kr.toxicity.healthbar.api.nms.NMS
import kr.toxicity.healthbar.api.pack.PackType
import kr.toxicity.healthbar.api.plugin.ReloadState
import kr.toxicity.healthbar.api.scheduler.WrappedScheduler
import kr.toxicity.healthbar.bedrock.FloodgateAdapter
import kr.toxicity.healthbar.bedrock.GeyserAdapter
import kr.toxicity.healthbar.manager.*
import kr.toxicity.healthbar.modelengine.CurrentModelEngineAdapter
import kr.toxicity.healthbar.modelengine.LegacyModelEngineAdapter
import kr.toxicity.healthbar.pack.PackGenerator
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.pack.PackUploader
import kr.toxicity.healthbar.scheduler.FoliaScheduler
import kr.toxicity.healthbar.scheduler.StandardScheduler
import kr.toxicity.healthbar.util.*
import kr.toxicity.healthbar.version.MinecraftVersion
import kr.toxicity.healthbar.version.ModelEngineVersion
import kr.toxicity.model.api.tracker.EntityTracker
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.text.DecimalFormat
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.jar.JarFile

@Suppress("UNUSED")
class BetterHealthBarImpl : BetterHealthBar() {

    private val isFolia = runCatching {
        Class.forName("io.papermc.paper.threadedregions.scheduler.FoliaAsyncScheduler")
        true
    }.getOrDefault(false)
    private val isPaper = isFolia || runCatching {
        Class.forName("com.destroystokyo.paper.profile.PlayerProfile")
        true
    }.getOrDefault(false)
    private var bedrock = BedrockAdapter.NONE
    private var model = ModelAdapter.NONE
    private lateinit var nms: NMS
    private lateinit var audiences: BukkitAudiences
    private val scheduler = if (isFolia) FoliaScheduler() else StandardScheduler()

    private val managers = listOf(
        EncodeManager,
        CompatibilityManager,
        ConfigManagerImpl,
        ListenerManagerImpl,
        PlaceholderManagerImpl,
        ImageManagerImpl,
        TextManagerImpl,
        LayoutManagerImpl,
        HealthBarManagerImpl,
        MobManagerImpl,
        PlayerManagerImpl,
    )

    @Volatile
    private var onReload = false

    override fun onEnable() {
        val log = ArrayList<String>()
        val manager = Bukkit.getPluginManager()
        nms = when (MinecraftVersion.current) {
            MinecraftVersion.version1_21_4 -> kr.toxicity.healthbar.nms.v1_21_R3.NMSImpl()
            MinecraftVersion.version1_21_2, MinecraftVersion.version1_21_3 -> kr.toxicity.healthbar.nms.v1_21_R2.NMSImpl()
            MinecraftVersion.version1_21, MinecraftVersion.version1_21_1 -> kr.toxicity.healthbar.nms.v1_21_R1.NMSImpl()
            MinecraftVersion.version1_20_5, MinecraftVersion.version1_20_6 -> kr.toxicity.healthbar.nms.v1_20_R4.NMSImpl()
            MinecraftVersion.version1_20_3, MinecraftVersion.version1_20_4 -> kr.toxicity.healthbar.nms.v1_20_R3.NMSImpl()
            MinecraftVersion.version1_20_2 -> kr.toxicity.healthbar.nms.v1_20_R2.NMSImpl()
            MinecraftVersion.version1_20, MinecraftVersion.version1_20_1 -> kr.toxicity.healthbar.nms.v1_20_R1.NMSImpl()
            MinecraftVersion.version1_19_4 -> kr.toxicity.healthbar.nms.v1_19_R3.NMSImpl()
            else -> {
                warn(
                    "Unsupported version found: ${MinecraftVersion.current}",
                    "Plugin will be disabled."
                )
                manager.disablePlugin(this)
                return
            }
        }
        manager.getPlugin("ModelEngine")?.let {
            runWithHandleException("Failed to load ModelEngine support.") {
                val version = ModelEngineVersion(it.description.version)
                model = if (version >= ModelEngineVersion.version_4_0_0) CurrentModelEngineAdapter() else LegacyModelEngineAdapter()
                log.add("ModelEngine support enabled: $version")
            }
        } ?: run {
            if (manager.isPluginEnabled("BetterModel")) model = ModelAdapter {
                EntityTracker.tracker(it.uniqueId)?.height()
            }
        }
        if (manager.isPluginEnabled("Geyser-Spigot")) {
            log.add("Geyser support enabled.")
            bedrock = GeyserAdapter()
        } else if (manager.isPluginEnabled("floodgate")) {
            log.add("Floodgate support enabled.")
            bedrock = FloodgateAdapter()
        }
        audiences = BukkitAudiences.create(this)
        getCommand("healthbar")?.setExecutor { commandSender, _, _, _ ->
            if (commandSender.hasPermission("betterhealthbar.reload")) {
                commandSender.sendMessage("Starts reloading. please wait...")
                CompletableFuture.runAsync {
                    when (val reload = reload()) {
                        is ReloadState.Success -> commandSender.sendMessage("Reload success! (${DecimalFormat.getInstance().format(reload.time)} ms)")
                        is ReloadState.Failure -> {
                            commandSender.sendMessage("Failed to reload.")
                            reload.throwable.handleException("Failed to reload.")
                        }
                        is ReloadState.OnReload -> commandSender.sendMessage("This plugin is still on reload.")
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
            if (!CompatibilityManager.usePackTypeNone || ConfigManagerImpl.packType() != PackType.NONE) scheduler.task {
                when (reload()) {
                    is ReloadState.Success -> info(*log.toTypedArray())
                    else -> {
                        manager.disablePlugin(this)
                    }
                }
            }
        }
        runWithHandleException("Unable to get latest version.") {
            HttpClient.newHttpClient().sendAsync(
                HttpRequest.newBuilder()
                    .uri(URI.create("https://api.spigotmc.org/legacy/update.php?resource=116619/"))
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString()
            ).thenAccept {
                val body = it.body()
                if (description.version != body) {
                    warn("New version found: $body")
                    warn("Download: https://www.spigotmc.org/resources/116619")
                    Bukkit.getPluginManager().registerEvents(object : Listener {
                        @EventHandler
                        fun join(e: PlayerJoinEvent) {
                            val player = e.player
                            if (player.isOp) {
                                player.info(Component.text("New BetterHealthBar version found: $body"))
                                player.info(Component.text("Download: https://www.spigotmc.org/resources/115559")
                                    .clickEvent(ClickEvent.clickEvent(
                                        ClickEvent.Action.OPEN_URL,
                                        "https://www.spigotmc.org/resources/115559"
                                    )))
                            }
                        }
                    }, this)
                }
            }
        }
    }

    override fun reload(): ReloadState {
        if (onReload) return ReloadState.ON_RELOAD
        val time = System.currentTimeMillis()
        onReload = true
        return runWithHandleException("Error has occurred while reloading.") {
            PackUploader.stop()
            managers.forEach {
                it.preReload()
            }
            val resource = PackResource()
            managers.forEach {
                debug("Reloading ${it.javaClass.simpleName}...")
                it.reload(resource)
            }
            managers.forEach {
                it.postReload()
            }
            onReload = false
            ReloadState.Success(System.currentTimeMillis() - time, PackGenerator.zip(ConfigManagerImpl.packType(), resource))
        }.getOrElse {
            onReload = false
            ReloadState.Failure(it)
        }
    }

    override fun onReload(): Boolean = onReload
    override fun bedrock(): BedrockAdapter = bedrock
    override fun miniMessage(): MiniMessage = MINI_MESSAGE
    override fun modelAdapter(): ModelAdapter = model
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
                    it.getInputStream(entry).buffered().use { stream ->
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
    override fun mobManager(): MobManager = MobManagerImpl
    override fun audiences(): BukkitAudiences = audiences
    override fun hookOtherShaders(): Boolean = CompatibilityManager.hookOtherShaders

    override fun onDisable() {
        runWithHandleException("Error has occurred while disabling.") {
            managers.forEach {
                it.end()
            }
            info("Plugin disabled.")
        }
    }
}