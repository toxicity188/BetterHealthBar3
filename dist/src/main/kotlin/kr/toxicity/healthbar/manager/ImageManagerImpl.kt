package kr.toxicity.healthbar.manager

import kr.toxicity.healthbar.api.image.HealthBarImage
import kr.toxicity.healthbar.api.image.ImageType
import kr.toxicity.healthbar.api.manager.ImageManager
import kr.toxicity.healthbar.image.HealthBarImageImpl
import kr.toxicity.healthbar.image.SplitType
import kr.toxicity.healthbar.pack.PackResource
import kr.toxicity.healthbar.util.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

object ImageManagerImpl : ImageManager, BetterHealthBerManager {

    private val imageMap = ConcurrentHashMap<String, HealthBarImageImpl>()
    private val framePattern = Pattern.compile("(?<name>(([a-zA-Z]|_|[0-9])+)):(?<frame>([0-9]+))")

    override fun image(name: String): HealthBarImage? {
        return imageMap[name]
    }

    override fun reload(resource: PackResource) {
        imageMap.clear()
        val images = resource.dataFolder.subFolder("assets")
        resource.dataFolder.subFolder("images").forEachAllYaml { file, s, section ->
            runWithHandleException("Unable to load this image: $s in ${file.path}") {
                val typeValue = section.getString("type").ifNull("Unable to find 'type' configuration.")
                val image = when (val type = ImageType.valueOf(typeValue.uppercase())) {
                    ImageType.SINGLE -> {
                        val name = section.getString("file").ifNull("Unable to find 'file' configuration.")
                            .replace('/', File.separatorChar)
                        val fileName = "${s}_${if (name.contains(File.separatorChar)) name.substringAfterLast(File.separatorChar) else name}"
                        HealthBarImageImpl(
                            file.path,
                            type,
                            listOf(File(images, name
                                .replace('/', File.separatorChar))
                                .requireExists()
                                .toImage()
                                .removeEmptyWidth()
                                .ifNull("Invalid image.")
                                .toNamed(fileName))
                        )
                    }
                    ImageType.LISTENER -> {
                        val splitType = section.getString("split-type").ifNull("Unable to find 'split-type' configuration.").run {
                            SplitType.valueOf(uppercase())
                        }
                        val name = section.getString("file").ifNull("Unable to find 'file' configuration.")
                            .replace('/', File.separatorChar)
                        val fileName = "${s}_${if (name.contains(File.separatorChar)) name.substringAfterLast(File.separatorChar) else name}"
                        HealthBarImageImpl(
                            file.path,
                            type,
                            splitType.split(File(images, name)
                                .requireExists()
                                .toImage()
                                .removeEmptyWidth()
                                .ifNull("Invalid image.")
                                .toNamed(fileName), section.getInt("split", 1).coerceAtLeast(1))
                        )
                    }
                    ImageType.SEQUENCE -> {
                        val mainFrame = section.getInt("frame", 1).coerceAtLeast(1)
                        HealthBarImageImpl(
                            file.path,
                            type,
                            section.getStringList("files").ifEmpty {
                                throw RuntimeException("'files' list is empty.")
                            }.map { t ->
                                var name = t.replace('/', File.separatorChar)
                                var frame = 1
                                val matcher = framePattern.matcher(name)
                                if (matcher.find()) {
                                    name = matcher.group("name")
                                    frame = matcher.group("frame").toInt()
                                }
                                val fileName = "${s}_${if (name.contains(File.separatorChar)) name.substringAfterLast(File.separatorChar) else name}"
                                val targetFile = File(images, name)
                                    .requireExists()
                                    .toImage()
                                    .removeEmptyWidth()
                                    .ifNull("Invalid image.")
                                    .toNamed(fileName)
                                (0..<(mainFrame * frame).coerceAtLeast(1)).map {
                                    targetFile
                                }
                            }.sum()
                        )
                    }
                }
                imageMap.putSync("image", s) {
                    image
                }
            }
        }
    }
}