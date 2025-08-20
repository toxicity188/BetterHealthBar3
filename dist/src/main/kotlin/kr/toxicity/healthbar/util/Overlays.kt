package kr.toxicity.healthbar.util

val OVERLAYS = listOf(
    PackOverlay(
        "betterhealthbar_legacy",
        VersionRange(13, 41)
    ),
    PackOverlay(
        "betterhealthbar_1_21_2",
        VersionRange(42, 55)
    ),
    PackOverlay(
        "betterhealthbar_1_21_6",
        VersionRange(56, 99)
    )
)

data class PackOverlay(
    val directory: String,
    val formats: VersionRange
) {
    fun toJson() = jsonObjectOf(
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