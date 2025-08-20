package kr.toxicity.healthbar.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kr.toxicity.healthbar.version.MinecraftVersion
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

val GSON: Gson = GsonBuilder().disableHtmlEscaping().create()

val PACK_MCMETA get() = jsonObjectOf(
    "pack" to jsonObjectOf(
        "pack_format" to MinecraftVersion.current.packVersion(),
        "description" to "BetterHealthBar's resource pack.",
        "supported_formats" to jsonArrayOf(13, 99)
    )
)
val PACK_MCMETA_WITH_OVERLAY get() = PACK_MCMETA.apply {
    add("overlays", jsonObjectOf(
        "entries" to jsonArrayOf(*OVERLAYS.map {
            it.toJson()
        }.toTypedArray())
    ))
}

fun InputStream.toJson(): JsonElement = InputStreamReader(this).buffered().use {
    JsonParser.parseReader(it)
}

fun File.toJson(): JsonElement = JsonReader(bufferedReader()).use {
    JsonParser.parseReader(it)
}

fun JsonElement.save(): ByteArray = ByteArrayOutputStream().let {
    JsonWriter(OutputStreamWriter(it, StandardCharsets.UTF_8).buffered()).use { os ->
        GSON.toJson(this, os)
    }
    it.toByteArray()
}

fun buildJsonArray(capacity: Int = 10, block: JsonArray.() -> Unit) = JsonArray(capacity).apply(block)
fun buildJsonObject(block: JsonObject.() -> Unit) = JsonObject().apply(block)

fun jsonArrayOf(vararg element: Any) = buildJsonArray {
    element.forEach {
        add(it.toJsonElement())
    }
}

fun jsonObjectOf(vararg element: Pair<String, Any>) = buildJsonObject {
    element.forEach {
        add(it.first, it.second.toJsonElement())
    }
}

operator fun JsonArray.plusAssign(other: JsonElement) {
    add(other)
}

fun Any.toJsonElement(): JsonElement = when (this) {
    is String -> JsonPrimitive(this)
    is Char -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is JsonElement -> this
    is List<*> -> run {
        val map = mapNotNull {
            it?.toJsonElement()
        }
        buildJsonArray(map.size) {
            map.forEach {
                add(it)
            }
        }
    }
    is Map<*, *> -> buildJsonObject {
        forEach {
            add(it.key?.toString() ?: return@forEach, it.value?.toJsonElement() ?: return@forEach)
        }
    }
    else -> throw RuntimeException("Unsupported type: ${javaClass.name}")
}
