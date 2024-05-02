package kr.toxicity.healthbar.pack

import kr.toxicity.healthbar.api.pack.PackType
import kr.toxicity.healthbar.manager.ConfigManagerImpl
import kr.toxicity.healthbar.util.NAMESPACE
import kr.toxicity.healthbar.util.PLUGIN
import kr.toxicity.healthbar.util.runWithHandleException
import kr.toxicity.healthbar.util.subFolder
import java.io.File
import java.io.OutputStream
import java.security.DigestOutputStream
import java.security.MessageDigest
import java.util.Collections
import java.util.Comparator
import java.util.TreeMap
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
            PLUGIN.loadAssets("pack") { s, i ->
                assetsMap.remove(s)
                File(parent, s).apply {
                    parentFile.mkdirs()
                }.outputStream().buffered().use { os ->
                    i.copyTo(os)
                }
            }
        }

        private val namespace = buildFolder.subFolder(NAMESPACE)
        private val font = namespace.subFolder("font")
        private val textures = namespace.subFolder("textures")

        override fun zip(resource: PackResource) {
            fun save(parent: File, target: PackResource.Builder) {
                assetsMap.remove(target.dir.replace(separator, '/'))
                File(parent, target.dir.replace('/', separator)).apply {
                    parentFile.mkdirs()
                }.outputStream().buffered().use { os ->
                    os.write(target.supplier())
                }
            }
            resource.font.forEachAsync {
                save(font, it)
            }
            resource.textures.forEachAsync {
                save(textures, it)
            }
        }
        override fun close() {
            assetsMap.values.forEach {
                it.delete()
            }
        }
    }

    private class ZipPack: Pack {

        private val zip = ConfigManagerImpl.buildFolder().run {
            File(parentFile.also {
                it.parentFile.mkdirs()
            }, name.let { n ->
                if (!n.endsWith(".zip")) "${n.substringBefore('.')}.zip" else n
            })
        }.apply {
            if (exists()) delete()
        }.outputStream().buffered().run {
            var stream: OutputStream = this
            runCatching {
                stream = DigestOutputStream(stream, MessageDigest.getInstance("SHA-1"))
            }
            ZipOutputStream(stream)
        }
        init {
            PLUGIN.loadAssets("pack") { s, i ->
                val read = i.readAllBytes()
                synchronized(zip) {
                    zip.putNextEntry(ZipEntry(s))
                    zip.write(read)
                    zip.closeEntry()
                }
            }
        }

        override fun zip(resource: PackResource) {
            fun save(prefix: String, target: PackResource.Builder) {
                val get = target.supplier()
                synchronized(zip) {
                    zip.putNextEntry(ZipEntry("$prefix/${target.dir}"))
                    zip.write(get)
                    zip.closeEntry()
                }
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
        }
    }


    fun zip(packType: PackType, resource: PackResource) {
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