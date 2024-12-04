package kr.toxicity.healthbar.version

import org.bukkit.Bukkit

data class MinecraftVersion(
    val first: Int,
    val second: Int,
    val third: Int,
): Comparable<MinecraftVersion> {
    companion object {
        val current = MinecraftVersion(Bukkit.getBukkitVersion()
            .substringBefore('-'))

        val version1_21_4 = MinecraftVersion(1, 21, 4)
        val version1_21_3 = MinecraftVersion(1, 21, 3)
        val version1_21_2 = MinecraftVersion(1, 21, 2)
        val version1_21_1 = MinecraftVersion(1, 21, 1)
        val version1_21 = MinecraftVersion(1, 21, 0)
        val version1_20_6 = MinecraftVersion(1, 20, 6)
        val version1_20_5 = MinecraftVersion(1, 20, 5)
        val version1_20_4 = MinecraftVersion(1, 20, 4)
        val version1_20_3 = MinecraftVersion(1, 20, 3)
        val version1_20_2 = MinecraftVersion(1, 20, 2)
        val version1_20_1 = MinecraftVersion(1, 20, 1)
        val version1_20 = MinecraftVersion(1, 20, 0)
        val version1_19_4 = MinecraftVersion(1, 19, 4)

        private val packVersion = mapOf(
            version1_21_4 to 46,
            version1_21_3 to 42,
            version1_21_2 to 42,
            version1_21_1 to 34,
            version1_21 to 34,
            version1_20_6 to 32,
            version1_20_5 to 32,
            version1_20_4 to 22,
            version1_20_3 to 22,
            version1_20_2 to 18,
            version1_20_1 to 15,
            version1_20 to 15,
            version1_19_4 to 13,
        )

        private val comparator = Comparator.comparing { v: MinecraftVersion ->
            v.first
        }.thenComparing { v: MinecraftVersion ->
            v.second
        }.thenComparing { v: MinecraftVersion ->
            v.third
        }
    }

    constructor(string: String): this(string.split('.'))
    constructor(string: List<String>): this(
        if (string.isNotEmpty()) string[0].toInt() else 0,
        if (string.size > 1) string[1].toInt() else 0,
        if (string.size > 2) string[2].toInt() else 0
    )
    override fun compareTo(other: MinecraftVersion): Int {
        return comparator.compare(this, other)
    }

    fun packVersion() = packVersion[this] ?: 7

    override fun toString(): String {
        return if (third != 0) "$first.$second.$third" else "$first.$second"
    }
}