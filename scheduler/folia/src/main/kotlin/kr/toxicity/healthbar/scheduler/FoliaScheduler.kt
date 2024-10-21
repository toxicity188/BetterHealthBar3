package kr.toxicity.healthbar.scheduler

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import kr.toxicity.healthbar.api.BetterHealthBar
import kr.toxicity.healthbar.api.scheduler.WrappedScheduler
import kr.toxicity.healthbar.api.scheduler.WrappedTask
import org.bukkit.Bukkit
import org.bukkit.World
import java.util.concurrent.TimeUnit

class FoliaScheduler : WrappedScheduler {

    private val plugin
        get() = BetterHealthBar.inst()

    override fun task(runnable: Runnable): WrappedTask {
        return Bukkit.getGlobalRegionScheduler().run(plugin) {
            runnable.run()
        }.wrap()
    }

    override fun asyncTask(runnable: Runnable): WrappedTask {
        return Bukkit.getAsyncScheduler().runNow(plugin) {
            runnable.run()
        }.wrap()
    }

    override fun taskLater(delay: Long, runnable: Runnable): WrappedTask {
        return Bukkit.getGlobalRegionScheduler().runDelayed(plugin, {
            runnable.run()
        }, delay).wrap()
    }

    override fun task(world: World, x: Int, y: Int, runnable: Runnable): WrappedTask {
        return Bukkit.getRegionScheduler().run(plugin, world, x, y) {
            runnable.run()
        }.wrap()
    }

    override fun asyncTaskTimer(delay: Long, period: Long, runnable: Runnable): WrappedTask {
        return Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
            runnable.run()
        }, delay * 50, period * 50, TimeUnit.MILLISECONDS).wrap()
    }

    private fun ScheduledTask.wrap() = WrappedTask {
        cancel()
    }
}