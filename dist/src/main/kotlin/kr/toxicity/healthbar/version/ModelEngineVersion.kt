package kr.toxicity.healthbar.version

data class ModelEngineVersion(val first: Int, val second: Int, val third: Int): Comparable<ModelEngineVersion> {
    companion object {
        private val comparator = Comparator.comparing { v: ModelEngineVersion ->
            v.first
        }.thenComparing { v ->
            v.second
        }.thenComparing { v ->
            v.third
        }

        val version_4_0_0 = ModelEngineVersion(4, 0, 0)
    }

    constructor(version: String): this(version.substring(1).split('.'))
    constructor(version: List<String>): this(
        if (version.isNotEmpty()) version[0].toInt() else 0,
        if (version.size > 1) version[1].toInt() else 0,
        if (version.size > 2) version[2].toInt() else 0
    )

    override fun toString(): String {
        return "$first.$second.$third"
    }

    override fun compareTo(other: ModelEngineVersion): Int {
        return comparator.compare(this, other)
    }
}