package kr.toxicity.healthbar.scheduler

import kr.toxicity.healthbar.api.BetterHealthBar
import kr.toxicity.healthbar.api.scheduler.WrappedScheduler
import kr.toxicity.healthbar.api.scheduler.WrappedTask
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.scheduler.BukkitTask

class StandardScheduler : WrappedScheduler {

    private val plugin
        get() = BetterHealthBar.inst()

    override fun task(runnable: Runnable): WrappedTask {
        return Bukkit.getScheduler().runTask(plugin, runnable).wrap()
    }
    override fun asyncTask(runnable: Runnable): WrappedTask {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable).wrap()
    }
    override fun taskLater(delay: Long, runnable: Runnable): WrappedTask {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delay).wrap()
    }
    override fun task(world: World, x: Int, y: Int, runnable: Runnable): WrappedTask {
        return task(runnable)
    }
    override fun asyncTaskTimer(delay: Long, period: Long, runnable: Runnable): WrappedTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period).wrap()
    }

    private fun BukkitTask.wrap() = WrappedTask {
        cancel()
    }
}