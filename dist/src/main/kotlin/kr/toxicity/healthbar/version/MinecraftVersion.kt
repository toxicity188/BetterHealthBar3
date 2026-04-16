package kr.toxicity.healthbar.version

import kr.toxicity.healthbar.util.toSemver
import org.bukkit.Bukkit

data class MinecraftVersion(
    val first: Int,
    val second: Int,
    val third: Int,
) : Comparable<MinecraftVersion> {
    companion object {
        private val comparator = Comparator.comparing { v: MinecraftVersion ->
            v.first
        }.thenComparing { v: MinecraftVersion ->
            v.second
        }.thenComparing { v: MinecraftVersion ->
            v.third
        }

        val current = of(Bukkit.getBukkitVersion()
            .substringBefore('-'))

        val version26_1_2 = MinecraftVersion(26, 1, 2)
        val version26_1_1 = MinecraftVersion(26, 1, 1)
        val version26_1 = MinecraftVersion(26, 1, 0)
        val version1_21_11 = MinecraftVersion(1, 21, 11)
        val version1_21_10 = MinecraftVersion(1, 21, 10)
        val version1_21_9 = MinecraftVersion(1, 21, 9)
        val version1_21_8 = MinecraftVersion(1, 21, 8)
        val version1_21_7 = MinecraftVersion(1, 21, 7)
        val version1_21_6 = MinecraftVersion(1, 21, 6)
        val version1_21_5 = MinecraftVersion(1, 21, 5)
        val version1_21_4 = MinecraftVersion(1, 21, 4)
        val version1_21_3 = MinecraftVersion(1, 21, 3)
        val version1_21_2 = MinecraftVersion(1, 21, 2)
        val version1_21_1 = MinecraftVersion(1, 21, 1)
        val version1_21 = MinecraftVersion(1, 21, 0)
        val version1_20_6 = MinecraftVersion(1, 20, 6)
        val version1_20_5 = MinecraftVersion(1, 20, 5)

        private val packVersion = mapOf(
            version26_1_2 to 84,
            version26_1_1 to 84,
            version26_1 to 84,
            version1_21_11 to 75,
            version1_21_10 to 69,
            version1_21_9 to 69,
            version1_21_8 to 64,
            version1_21_7 to 64,
            version1_21_6 to 63,
            version1_21_5 to 55,
            version1_21_4 to 46,
            version1_21_3 to 42,
            version1_21_2 to 42,
            version1_21_1 to 34,
            version1_21 to 34,
            version1_20_6 to 32,
            version1_20_5 to 32
        )

        fun of(target: String) = target.toSemver().run {
            MinecraftVersion(major, minor, patch)
        }
    }

    val canUseShadowColor get() = this >= version1_21_4

    override fun compareTo(other: MinecraftVersion): Int {
        return comparator.compare(this, other)
    }

    fun packVersion() = packVersion[this] ?: 7

    override fun toString(): String {
        return if (third != 0) "$first.$second.$third" else "$first.$second"
    }
}