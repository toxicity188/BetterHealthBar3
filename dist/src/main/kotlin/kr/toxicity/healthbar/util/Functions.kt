package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.api.event.HealthBarCreateEvent
import kr.toxicity.healthbar.api.placeholder.PlaceholderBuilder
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import java.util.function.Function

fun <T> T?.ifNull(message: String): T & Any = this ?: throw RuntimeException(message)

fun <T> runWithHandleException(message: String, block: () -> T) = runCatching(block).onFailure {
    warn(*it.handleException(message))
}

fun Throwable.handleException(log: String): Array<String> {
    val list = mutableListOf(
        log,
        "Reason: $message"
    )
    if (ConfigManagerImpl.debug()) list += listOf(
        "Stack trace:",
        stackTraceToString()
    )
    return list.toTypedArray()
}

inline fun <reified T : Any> placeholder(length: Int, function: Function<List<String>, Function<HealthBarCreateEvent, T>>): PlaceholderBuilder.Builder<T> {
    return PlaceholderBuilder.builder(T::class.java).ifNull("Unable to find this type: ${T::class.java.simpleName}")
        .argsLength(length)
        .parser(function)
}