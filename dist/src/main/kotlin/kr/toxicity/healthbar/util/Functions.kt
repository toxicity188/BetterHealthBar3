package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.manager.ConfigManagerImpl

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