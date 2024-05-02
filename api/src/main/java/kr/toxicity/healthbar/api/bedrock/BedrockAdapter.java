package kr.toxicity.healthbar.api.bedrock;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface BedrockAdapter {
    BedrockAdapter NONE = u -> false;
    boolean isBedrockPlayer(@NotNull UUID uuid);
}
