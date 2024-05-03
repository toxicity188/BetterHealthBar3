package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.api.healthbar.HealthBarPair
import kr.toxicity.healthbar.api.placeholder.PlaceholderBuilder
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import java.util.function.Function

fun <T> T?.ifNull(message: String): T & Any = this ?: throw RuntimeException(message)

fun <T> runWithHandleException(message: String, block: () -> T) = runCatching(block).onFailure {
    warn(*it.handleException(message))
}

fun Throwable.handleException(log: String): Array<String> {
    if (ConfigManagerImpl.debug()) printStackTrace()
    return arrayOf(
        log,
        "Reason: $message"
    )
}

inline fun <reified T> placeholder(length: Int, function: Function<List<String>, Function<HealthBarPair, T>>): PlaceholderBuilder<T> {
    return PlaceholderBuilder.of(length, T::class.java, function)
}