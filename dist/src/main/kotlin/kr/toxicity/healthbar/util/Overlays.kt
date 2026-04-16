package kr.toxicity.healthbar.util

val OVERLAYS = listOf(
    PackOverlay(
        "betterhealthbar_legacy",
        VersionRange(32, 41)
    ),
    PackOverlay(
        "betterhealthbar_1_21_2",
        VersionRange(42, 55)
    ),
    PackOverlay(
        "betterhealthbar_1_21_6",
        VersionRange(56, 83)
    ),
    PackOverlay(
        "betterhealthbar_26_1",
        VersionRange(84, 99)
    )
)

data class PackOverlay(
    val directory: String,
    val formats: VersionRange
) {
    fun toJson() = jsonObjectOf(
        "min_format" to formats.min,
        "max_format" to formats.max,
        "formats" to formats.toJson(),
        "directory" to directory
    )
}

data class VersionRange(
    val min: Int,
    val max: Int
) {
    fun toJson() = jsonArrayOf(min, max)
}