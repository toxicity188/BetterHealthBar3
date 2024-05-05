package kr.toxicity.healthbar.pack

import kr.toxicity.healthbar.api.pack.PackType
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.util.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.security.DigestOutputStream
import java.security.MessageDigest
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object PackGenerator {

    private val separator = File.separatorChar

    private interface Pack: AutoCloseable {
        fun zip(resource: PackResource)
    }

    private class FolderPack: Pack {
        private val parent = ConfigManagerImpl.buildFolder()
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

        override fun zip(resource: PackResource) {
            fun save(parent: File, target: PackResource.Builder, remove: String) {
                (assetsMap.remove(if (remove.isNotEmpty()) "$remove/${target.dir}" else target.dir) ?: File(parent, target.dir.replace('/', separator)).apply {
                    parentFile.mkdirs()
                }).outputStream().buffered().use { os ->
                    os.write(target.supplier())
                }
            }
            resource.merge.forEachAsync {
                save(parent, it, "")
            }
            fun applyResource(s: String, i: InputStream) {
                assetsMap.remove(s)
                File(parent, s).apply {
                    parentFile.mkdirs()
                }.outputStream().buffered().use { os ->
                    i.copyTo(os)
                }
            }
            if (ConfigManagerImpl.shaders().renderTypeFragment) PLUGIN.getResource("rendertype_text.fsh")?.buffered()?.use {
                applyResource("assets/minecraft/shaders/core/rendertype_text.fsh", it)
            }
            if (ConfigManagerImpl.shaders().renderTypeVertex) PLUGIN.getResource("rendertype_text.vsh")?.buffered()?.use {
                applyResource("assets/minecraft/shaders/core/rendertype_text.vsh", it)
            }
            resource.font.forEachAsync {
                save(font, it, "assets/$NAMESPACE/font")
            }
            resource.textures.forEachAsync {
                save(textures, it, "assets/$NAMESPACE/textures")
            }
        }
        override fun close() {
            assetsMap.values.forEach {
                it.delete()
            }
        }
    }

    private class ZipPack: Pack {

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
        override fun zip(resource: PackResource) {
            fun save(prefix: String, target: PackResource.Builder) {
                val get = target.supplier()
                val name = if (prefix.isNotEmpty()) "$prefix/${target.dir}" else target.dir
                synchronized(zip) {
                    zip.putNextEntry(ZipEntry(name))
                    zip.write(get)
                    zip.closeEntry()
                }
            }
            resource.merge.forEachAsync {
                save("", it)
            }
            fun applyResource(s: String, i: InputStream) {
                val read = i.readAllBytes()
                synchronized(zip) {
                    zip.putNextEntry(ZipEntry(s))
                    zip.write(read)
                    zip.closeEntry()
                }
            }
            if (ConfigManagerImpl.shaders().renderTypeFragment) PLUGIN.getResource("rendertype_text.fsh")?.buffered()?.use {
                applyResource("assets/minecraft/shaders/core/rendertype_text.fsh", it)
            }
            if (ConfigManagerImpl.shaders().renderTypeVertex) PLUGIN.getResource("rendertype_text.vsh")?.buffered()?.use {
                applyResource("assets/minecraft/shaders/core/rendertype_text.vsh", it)
            }
            resource.font.forEachAsync {
                save("assets/$NAMESPACE/font", it)
            }
            resource.textures.forEachAsync {
                save("assets/$NAMESPACE/textures", it)
            }
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


    fun zip(packType: PackType, resource: PackResource) {
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
        runWithHandleException("Unable to zip resource pack.") {
            when (packType) {
                PackType.FOLDER -> FolderPack()
                PackType.ZIP -> ZipPack()
            }.use {
                it.zip(resource)
            }
        }
    }
}