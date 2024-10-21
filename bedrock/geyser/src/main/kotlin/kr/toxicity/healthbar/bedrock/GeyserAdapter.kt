package kr.toxicity.healthbar.bedrock

import kr.toxicity.healthbar.api.bedrock.BedrockAdapter
import org.geysermc.api.Geyser
import java.util.*

class GeyserAdapter : BedrockAdapter {
    override fun isBedrockPlayer(uuid: UUID): Boolean {
        return Geyser.api().isBedrockPlayer(uuid)
    }
}
