package kr.toxicity.healthbar.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

val GSON: Gson = GsonBuilder().disableHtmlEscaping().create()

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