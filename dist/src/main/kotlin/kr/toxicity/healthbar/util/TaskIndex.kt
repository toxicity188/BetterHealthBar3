package kr.toxicity.healthbar.util

class TaskIndex(val max: Int) {
    @Volatile
    var current = 0
}