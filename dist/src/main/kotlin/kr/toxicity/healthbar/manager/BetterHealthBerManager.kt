package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.pack.PackResource

interface BetterHealthBerManager {
    fun start() {}
    fun preReload() {}
    fun reload(resource: PackResource)
    fun postReload() {}
    fun end() {}
}