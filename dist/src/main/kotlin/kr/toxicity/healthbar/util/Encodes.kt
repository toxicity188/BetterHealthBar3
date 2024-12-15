package kr.toxicity.healthbar.util

import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.manager.EncodeManager
import net.kyori.adventure.key.Key

fun String.encodeFile(path: EncodeManager.EncodeNamespace): String {
    val split = split('.')
    if (split.size != 2) throw RuntimeException("Invaild file name: $this")
    return "${encodeKey(path, split[0])}.${split[1]}"
}

fun encodeKey(path: EncodeManager.EncodeNamespace, name: String) = if (ConfigManagerImpl.resourcePackObfuscation()) EncodeManager.generateKey(path, name) else name

fun createAdventureKey(path: String) = Key.key(NAMESPACE, path)