package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.api.BetterHealthBar
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import org.bukkit.Bukkit
import org.bukkit.event.Listener

val PLUGIN
    get() = BetterHealthBar.inst()

val DATA_FOLDER
    get() = PLUGIN.dataFolder.apply {
        if (!exists()) {
            mkdir()
            PLUGIN.loadAssets("default", this)
        }
    }

val NAMESPACE
    get() = ConfigManagerImpl.namespace()

fun registerListeners(listener: Listener) = Bukkit.getPluginManager().registerEvents(listener, PLUGIN)

fun info(vararg message: String) {
    val logger = PLUGIN.logger
    synchronized(logger) {
        message.forEach {
            logger.info(it)
        }
    }
}

fun warn(vararg message: String) {
    val logger = PLUGIN.logger
    synchronized(logger) {
        message.forEach {
            logger.warning(it)
        }
    }
}

fun debug(vararg message: String) {
    if (ConfigManagerImpl.debug()) info(*message)
}

fun taskLater(delay: Long, block: () -> Unit) = PLUGIN.scheduler().taskLater(delay, block)
fun asyncTaskTimer(delay: Long, period: Long, block: () -> Unit) = PLUGIN.scheduler().asyncTaskTimer(delay, period, block)