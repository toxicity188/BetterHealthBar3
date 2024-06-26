package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.api.configuration.HealthBarConfiguration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashSet

private val CACHE_MAP = ConcurrentHashMap<String, MutableSet<String>>()

private fun getCache(name: String) = CACHE_MAP.computeIfAbsent(name) {
    Collections.synchronizedSet(HashSet())
}

fun <V: HealthBarConfiguration> MutableMap<String, V>.putSync(name: String, k: String, v: () -> V) {
    val cache = getCache(name)
    fun warn() = warn("Name collision found: $k in $name")
    fun remove() {
        cache.remove(k)
        if (cache.isEmpty()) CACHE_MAP.remove(name)
    }
    if (!cache.add(k)) {
        if (cache.isEmpty()) CACHE_MAP.remove(name)
        return warn()
    }
    runCatching {
        synchronized(this) {
            get(k)
        }?.let {
            warn()
            return remove()
        }
        val get = v()
        synchronized(this) {
            putIfAbsent(k, get)?.let {
                warn("Error has been occurred: ${it.path()} and ${get.path()}")
            }
            remove()
        }
    }.onFailure {
        remove()
        throw it
    }
}