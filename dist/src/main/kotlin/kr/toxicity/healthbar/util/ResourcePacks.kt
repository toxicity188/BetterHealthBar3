package kr.toxicity.healthbar.util

import java.util.regex.Pattern

private val VALID_RESOURCE_PACK_REGEX = Pattern.compile("(([a-zA-Z]|[0-9]|_)+)")

fun String.isValidPackNamespace() = VALID_RESOURCE_PACK_REGEX.matcher(this).find()