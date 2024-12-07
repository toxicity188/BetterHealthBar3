package kr.toxicity.healthbar.pack

import kr.toxicity.healthbar.api.pack.PackType
import kr.toxicity.healthbar.manager.CompatibilityManager
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.util.*
import java.io.File
import java.io.OutputStream
import java.security.DigestOutputStream
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object PackGenerator {

    private val separator = File.separatorChar

    private interface Pack : AutoCloseable {
        fun zip(resource: PackResource): Map<String, ByteArray>
    }

    private fun loadShaders(consumer: (String, ByteArray) -> Unit) {
        if (ConfigManagerImpl.useCoreShaders() && ! CompatibilityManager.hookOtherShaders) {
            if (ConfigManagerImpl.shaders().renderTypeJson) PLUGIN.getResource("rendertype_text.json")?.buffered()?.use {
                consumer("assets/minecraft/shaders/core/rendertype_text.json", it.readAllBytes())
            }
            if (ConfigManagerImpl.shaders().renderTypeFragment) PLUGIN.getResource("text.fsh")?.buffered()?.use {
                val read = it.readAllBytes()
                consumer("assets/minecraft/shaders/core/text.fsh", read)
                consumer("assets/minecraft/shaders/text.fsh", read)
            }
            if (ConfigManagerImpl.shaders().renderTypeVertex) PLUGIN.getResource("text.vsh")?.buffered()?.use {
                val read = it.readAllBytes()
                consumer("assets/minecraft/shaders/core/text.vsh", read)
                consumer("assets/minecraft/shaders/text.vsh", read)
            }
        }
    }

    private class NonePack : Pack {
        override fun zip(resource: PackResource): Map<String, ByteArray> {
            val byteMap = ConcurrentHashMap<String, ByteArray>()
            loadShaders { s, bytes ->
                byteMap[s] = bytes
            }
            fun save(target: PackResource.Builder, remove: String) {
                val n = if (remove.isNotEmpty()) "$remove/${target.dir}" else target.dir
                val get = target.supplier()
                byteMap[n] = get
            }
            resource.font.forEachAsync {
                save(it, "assets/$NAMESPACE/font")
            }
            resource.textures.forEachAsync {
                save(it, "assets/$NAMESPACE/textures")
            }
            return byteMap
        }

        override fun close() {

        }
    }

    private class FolderPack : Pack {
        private val parent: File = ConfigManagerImpl.buildFolder()
        private val buildFolder = File(parent, "assets").apply {
            mkdirs()
        }

        private val assetsMap = Collections.synchronizedMap(TreeMap<String, File>(Comparator.reverseOrder()))

        init {
            fun append(parent: String, name: String, file: File) {
                val n = if (parent.isNotEmpty()) "$parent/$name" else name
                if (n.isNotEmpty()) assetsMap["$n/${file.name}"] = file
                if (file.isDirectory) {
                    file.listFiles()?.forEach {
                        append(n, file.name, it)
                    }
                }
            }
            append("", "", buildFolder)
        }

        private val namespace = buildFolder.subFolder(NAMESPACE)
        private val font = namespace.subFolder("font")
        private val textures = namespace.subFolder("textures")

        override fun zip(resource: PackResource): Map<String, ByteArray> {
            val byteMap = ConcurrentHashMap<String, ByteArray>()
            fun save(parent: File, target: PackResource.Builder, remove: String) {
                val n = if (remove.isNotEmpty()) "$remove/${target.dir}" else target.dir
                val get = target.supplier()
                byteMap[n] = get
                (assetsMap.remove(n) ?: File(parent, target.dir.replace('/', separator)).apply {
                    parentFile.mkdirs()
                }).outputStream().buffered().use { os ->
                    os.write(get)
                }
            }
            resource.merge.forEachAsync {
                save(parent, it, "")
            }
            loadShaders { s, byteArray ->
                byteMap[s] = byteArray
                assetsMap.remove(s)
                File(parent, s).apply {
                    parentFile.mkdirs()
                }.outputStream().buffered().use { os ->
                    os.write(byteArray)
                }
            }
            resource.font.forEachAsync {
                save(font, it, "assets/$NAMESPACE/font")
            }
            resource.textures.forEachAsync {
                save(textures, it, "assets/$NAMESPACE/textures")
            }
            return byteMap
        }
        override fun close() {
            assetsMap.values.forEach {
                it.delete()
            }
        }
    }

    private class ZipPack : Pack {

        private val file = ConfigManagerImpl.buildFolder().run {
            File(parentFile.also {
                it.parentFile.mkdirs()
            }, name.let { n ->
                if (!n.endsWith(".zip")) "${n.substringBefore('.')}.zip" else n
            })
        }.apply {
            if (exists()) delete()
        }
        private val digest = runCatching {
            MessageDigest.getInstance("SHA-1")
        }.getOrNull()
        private val zip = file.apply {
            if (exists()) delete()
        }.outputStream().buffered().run {
            var stream: OutputStream = this
            digest?.let {
                stream = DigestOutputStream(stream, it)
            }
            ZipOutputStream(stream)
        }
        override fun zip(resource: PackResource): Map<String, ByteArray> {
            val byteMap = ConcurrentHashMap<String, ByteArray>()
            fun save(prefix: String, target: PackResource.Builder) {
                val get = target.supplier()
                val name = if (prefix.isNotEmpty()) "$prefix/${target.dir}" else target.dir
                byteMap[name] = get
                synchronized(zip) {
                    zip.putNextEntry(ZipEntry(name))
                    zip.write(get)
                    zip.closeEntry()
                }
            }
            resource.merge.forEachAsync {
                save("", it)
            }
            loadShaders { s: String, byte: ByteArray ->
                byteMap[s] = byte
                synchronized(zip) {
                    zip.putNextEntry(ZipEntry(s))
                    zip.write(byte)
                    zip.closeEntry()
                }
            }
            resource.font.forEachAsync {
                save("assets/$NAMESPACE/font", it)
            }
            resource.textures.forEachAsync {
                save("assets/$NAMESPACE/textures", it)
            }
            return byteMap
        }

        override fun close() {
            zip.close()
            if (ConfigManagerImpl.enableSelfHost()) {
                digest?.let { digest ->
                    file.inputStream().buffered().use {
                        PackUploader.upload(digest, it.readAllBytes())
                    }
                }
            }
        }
    }


    fun zip(packType: PackType, resource: PackResource): Map<String, ByteArray> {
        fun add(file: File, path: String) {
            val name = if (path.isEmpty()) file.name else "$path/${file.name}"
            if (file.isDirectory) file.listFiles()?.forEach {
                add(it, name)
            } else {
                resource.merge.add(name) {
                    file.inputStream().buffered().use {
                        it.readAllBytes()
                    }
                }
            }
        }
        val parent = DATA_FOLDER.parentFile
        ConfigManagerImpl.mergeOtherFolder().forEach {
            File(parent, it).listFiles()?.forEach { f ->
                add(f, "")
            }
        }
        return runWithHandleException("Unable to zip resource pack.") {
            when (packType) {
                PackType.NONE -> NonePack()
                PackType.FOLDER -> FolderPack()
                PackType.ZIP -> ZipPack()
            }.use {
                it.zip(resource)
            }
        }.getOrDefault(emptyMap())
    }
}