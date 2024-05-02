package kr.toxicity.healthbar.image

import kr.toxicity.healthbar.api.image.HealthBarImage
import kr.toxicity.healthbar.api.image.ImageType
import kr.toxicity.healthbar.api.image.NamedProcessedImage

class HealthBarImageImpl(
    private val path: String,
    private val type: ImageType,
    private val list: List<NamedProcessedImage>
): HealthBarImage {
    override fun path(): String = path
    override fun type(): ImageType = type
    override fun images(): List<NamedProcessedImage> = list
}